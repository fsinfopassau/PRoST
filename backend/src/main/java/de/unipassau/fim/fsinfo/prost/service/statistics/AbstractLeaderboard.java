package de.unipassau.fim.fsinfo.prost.service.statistics;

import de.unipassau.fim.fsinfo.prost.data.TimeSpan;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractLeaderboard<T> {

  public static final long MONTH_MILLIS = 1000L * 60 * 60 * 24 * 30;

  private static final Map<Class<?>, List<AbstractLeaderboard<?>>> REGISTRY = new ConcurrentHashMap<>();

  public static <T> void updateAllEntriesFor(Class<T> entityType, T entity) {
    List<AbstractLeaderboard<?>> leaderboards = REGISTRY.getOrDefault(entityType, List.of());

    for (AbstractLeaderboard<?> leaderboard : leaderboards) {
      if (leaderboard.supportsEntityType(entityType)) {
        @SuppressWarnings("unchecked")
        AbstractLeaderboard<T> typedLeaderboard = (AbstractLeaderboard<T>) leaderboard;
        typedLeaderboard.updateEntry(entity);
      }
    }
  }

  // Static method to remove all entries for a specific entity type
  public static <T> void removeAllEntriesFor(Class<T> entityType, T entity) {
    List<AbstractLeaderboard<?>> leaderboards = REGISTRY.getOrDefault(entityType, List.of());
    for (AbstractLeaderboard<?> leaderboard : leaderboards) {
      if (leaderboard.supportsEntityType(entityType)) {
        @SuppressWarnings("unchecked")
        AbstractLeaderboard<T> typedLeaderboard = (AbstractLeaderboard<T>) leaderboard;
        typedLeaderboard.removeEntry(entity);
      }
    }
  }

  // hardcoded to the last 30days is easier to calculate and more consistent over time.
  private ConcurrentHashMap<T, BigDecimal> leaderboardEntries_Monthly = new ConcurrentHashMap<>();
  private ConcurrentHashMap<T, BigDecimal> leaderboardEntries_AllTime = new ConcurrentHashMap<>();

  private final Class<T> entityType;

  public AbstractLeaderboard(Class<T> entityType) {
    this.entityType = entityType;
    REGISTRY.computeIfAbsent(entityType, k -> new ArrayList<>()).add(this);
  }

  public void initLeaderboard(Collection<T> initialEntries) {
    leaderboardEntries_Monthly = new ConcurrentHashMap<>();
    leaderboardEntries_AllTime = new ConcurrentHashMap<>();
    initialEntries.forEach(this::updateEntry);
  }

  public boolean supportsEntityType(Class<?> type) {
    return this.entityType.equals(type);
  }

  public abstract BigDecimal calculateValue(T entity, Long startTimestamp, Long endTimestamp);

  private void updateEntry(T entity) {
    long now = Instant.now().toEpochMilli();
    leaderboardEntries_Monthly.put(entity, calculateValue(entity, now - MONTH_MILLIS, now));
    leaderboardEntries_AllTime.put(entity, calculateValue(entity, 0L, now));
  }

  private void removeEntry(T entity) {
    leaderboardEntries_Monthly.remove(entity);
    leaderboardEntries_AllTime.remove(entity);
  }

  public Optional<List<LeaderboardEntry<T>>> getLeaderboardEntries(TimeSpan timeSpan) {
    return switch (timeSpan) {
      case MONTH -> Optional.of(mapToLeaderboardEntries(leaderboardEntries_Monthly));
      case ALL_TIME -> Optional.of(mapToLeaderboardEntries(leaderboardEntries_AllTime));
    };
  }

  private List<LeaderboardEntry<T>> mapToLeaderboardEntries(
      ConcurrentHashMap<T, BigDecimal> leaderboardEntries) {

    return leaderboardEntries.entrySet().stream()
        .sorted(Map.Entry.<T, BigDecimal>comparingByValue().reversed())
        .map(entry -> new LeaderboardEntry<T>(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  public record LeaderboardEntry<T>(T item, BigDecimal value) {

  }

}
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
  public static final long WEEK_MILLIS = 1000L * 60 * 60 * 24 * 7;

  protected static final Map<Class<?>, List<AbstractLeaderboard<?>>> REGISTRY = new ConcurrentHashMap<>();

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
  protected ConcurrentHashMap<String, BigDecimal> leaderboardEntries_Weekly = new ConcurrentHashMap<>();
  protected ConcurrentHashMap<String, BigDecimal> leaderboardEntries_Monthly = new ConcurrentHashMap<>();
  protected ConcurrentHashMap<String, BigDecimal> leaderboardEntries_AllTime = new ConcurrentHashMap<>();

  protected final Class<T> entityType;

  public AbstractLeaderboard(Class<T> entityType) {
    this.entityType = entityType;
    REGISTRY.computeIfAbsent(entityType, k -> new ArrayList<>()).add(this);
  }

  public void initLeaderboard(Collection<T> initialEntries) {
    leaderboardEntries_Weekly = new ConcurrentHashMap<>();
    leaderboardEntries_Monthly = new ConcurrentHashMap<>();
    leaderboardEntries_AllTime = new ConcurrentHashMap<>();
    initialEntries.forEach(this::updateEntry);
  }

  public boolean supportsEntityType(Class<?> type) {
    return this.entityType.equals(type);
  }

  public abstract BigDecimal calculateValue(T entity, Long startTimestamp, Long endTimestamp);

  public abstract String getKey(T entity);

  public abstract T findByKey(String key);

  protected void updateEntry(T entity) {
    long now = Instant.now().toEpochMilli();
    leaderboardEntries_Weekly.put(getKey(entity), calculateValue(entity, now - WEEK_MILLIS, now));
    leaderboardEntries_Monthly.put(getKey(entity), calculateValue(entity, now - MONTH_MILLIS, now));
    leaderboardEntries_AllTime.put(getKey(entity), calculateValue(entity, 0L, now));
  }

  protected void removeEntry(T entity) {
    leaderboardEntries_Weekly.remove(getKey(entity));
    leaderboardEntries_Monthly.remove(getKey(entity));
    leaderboardEntries_AllTime.remove(getKey(entity));
  }

  public Optional<List<LeaderboardEntry<T>>> getLeaderboardEntries(TimeSpan timeSpan) {
    return switch (timeSpan) {
      case WEEK -> Optional.of(mapToLeaderboardEntries(leaderboardEntries_Weekly));
      case MONTH -> Optional.of(mapToLeaderboardEntries(leaderboardEntries_Monthly));
      case ALL_TIME -> Optional.of(mapToLeaderboardEntries(leaderboardEntries_AllTime));
    };
  }

  protected List<LeaderboardEntry<T>> mapToLeaderboardEntries(
      ConcurrentHashMap<String, BigDecimal> leaderboardEntries) {

    return leaderboardEntries.entrySet().stream()
        .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
        .map(entry -> new LeaderboardEntry<>(entry.getKey(), findByKey(entry.getKey()),
            entry.getValue()))
        .collect(Collectors.toList());
  }


  public record LeaderboardEntry<T>(String key, T entity, BigDecimal value) {

  }

}
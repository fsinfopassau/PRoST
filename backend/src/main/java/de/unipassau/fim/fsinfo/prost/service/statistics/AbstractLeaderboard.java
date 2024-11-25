package de.unipassau.fim.fsinfo.prost.service.statistics;

import de.unipassau.fim.fsinfo.prost.data.TimeSpan;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractLeaderboard<T> {

  public static final long MONTH_MILLIS = 60 * 60 * 24 * 30;

  // hardcoded to the last 30days is easier to calculate and more consistent over time.
  private ConcurrentHashMap<T, BigDecimal> leaderboardEntries_Monthly = new ConcurrentHashMap<>();
  private ConcurrentHashMap<T, BigDecimal> leaderboardEntries_AllTime = new ConcurrentHashMap<>();

  public void initLeaderboard(Collection<T> initialEntries) {
    initialEntries.forEach(this::updateEntry);
  }

  public abstract BigDecimal calculateValue(T entity, Long startTimestamp, Long endTimestamp);

  public void updateEntry(T entity) {
    long now = Instant.now().toEpochMilli();
    leaderboardEntries_Monthly.put(entity, calculateValue(entity, now - MONTH_MILLIS, now));
    leaderboardEntries_AllTime.put(entity, calculateValue(entity, 0L, now));
  }

  public void removeEntry(T entity) {
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

  @Getter
  @AllArgsConstructor
  public static class LeaderboardEntry<T> {

    private final T item;
    private final BigDecimal value;
  }

}
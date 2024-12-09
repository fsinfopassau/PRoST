package de.unipassau.fim.fsinfo.prost.service.statistics;

import de.unipassau.fim.fsinfo.prost.data.metrics.TimeSpan;
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

/**
 * @param <T> The type of MetricEntry that will be returned.
 * @param <A> The type of Object that triggers an update.
 */
@Service
public abstract class AbstractMetricCollector<T, A> {

  public static final long MONTH_MILLIS = 1000L * 60 * 60 * 24 * 30;
  public static final long WEEK_MILLIS = 1000L * 60 * 60 * 24 * 7;

  protected static final Map<Class<?>, List<AbstractMetricCollector<?, ?>>> REGISTRY = new ConcurrentHashMap<>();

  public static <T, A> void initAllCollectors(Class<A> entityType, Collection<A> initialEntries) {
    List<AbstractMetricCollector<?, ?>> metrics = REGISTRY.getOrDefault(entityType, List.of());

    for (AbstractMetricCollector<?, ?> metric : metrics) {
      if (metric.supportsEntityType(entityType)) {
        @SuppressWarnings("unchecked")
        AbstractMetricCollector<T, A> typedMetric = (AbstractMetricCollector<T, A>) metric;
        typedMetric.initMetrics(initialEntries);
      }
    }
  }

  public static <A> void updateAllEntriesFor(Class<A> entityType, A entity) {
    List<AbstractMetricCollector<?, ?>> metrics = REGISTRY.getOrDefault(entityType, List.of());

    for (AbstractMetricCollector<?, ?> metric : metrics) {
      if (metric.supportsEntityType(entityType)) {
        @SuppressWarnings("unchecked")
        AbstractMetricCollector<?, A> typedMetric = (AbstractMetricCollector<?, A>) metric;
        typedMetric.updateEntry(entity);
      }
    }
  }

  // Static method to remove all entries for a specific entity type
  public static <A> void removeAllEntriesFor(Class<A> entityType, A entity) {
    List<AbstractMetricCollector<?, ?>> metrics = REGISTRY.getOrDefault(entityType, List.of());
    for (AbstractMetricCollector<?, ?> metric : metrics) {
      if (metric.supportsEntityType(entityType)) {
        @SuppressWarnings("unchecked")
        AbstractMetricCollector<?, A> typedmetric = (AbstractMetricCollector<?, A>) metric;
        typedmetric.removeEntry(entity);
      }
    }
  }

  // hardcoded to the last 30days is easier to calculate and more consistent over time.
  protected ConcurrentHashMap<String, BigDecimal> metricEntries_Weekly = new ConcurrentHashMap<>();
  protected ConcurrentHashMap<String, BigDecimal> metricEntries_Monthly = new ConcurrentHashMap<>();
  protected ConcurrentHashMap<String, BigDecimal> metricEntries_AllTime = new ConcurrentHashMap<>();

  protected final Class<A> entityType;

  public AbstractMetricCollector(Class<A> entityType) {
    this.entityType = entityType;
    REGISTRY.computeIfAbsent(entityType, k -> new ArrayList<>()).add(this);
  }

  protected void initMetrics(Collection<A> initialEntries) {
    metricEntries_Weekly = new ConcurrentHashMap<>();
    metricEntries_Monthly = new ConcurrentHashMap<>();
    metricEntries_AllTime = new ConcurrentHashMap<>();
    initialEntries.forEach(this::updateEntry);
  }

  protected boolean supportsEntityType(Class<?> type) {
    return this.entityType.equals(type);
  }

  /**
   * Calculate only new entry and add value to previous, because it minimizes db-access (when
   * possible)
   */
  protected abstract BigDecimal calculateValue(A entity, TimeSpan timeSpan, Long startTimestamp,
      Long endTimestamp);

  protected abstract String getKey(A entity);

  protected abstract T findByKey(String key);

  protected void updateEntry(A entity) {
    long now = Instant.now().toEpochMilli();
    metricEntries_Weekly.put(getKey(entity),
        calculateValue(entity, TimeSpan.WEEK, now - WEEK_MILLIS, now));
    metricEntries_Monthly.put(getKey(entity),
        calculateValue(entity, TimeSpan.MONTH, now - MONTH_MILLIS, now));
    metricEntries_AllTime.put(getKey(entity), calculateValue(entity, TimeSpan.ALL_TIME, 0L, now));
  }

  protected void removeEntry(A entity) {
    metricEntries_Weekly.remove(getKey(entity));
    metricEntries_Monthly.remove(getKey(entity));
    metricEntries_AllTime.remove(getKey(entity));
  }

  protected Optional<BigDecimal> getValue(TimeSpan timeSpan, String key) {
    BigDecimal value;
    switch (timeSpan) {
      case WEEK -> value = metricEntries_Weekly.get(key);
      case MONTH -> value = metricEntries_Monthly.get(key);
      case ALL_TIME -> value = metricEntries_AllTime.get(key);
      default -> throw new IllegalStateException("Unexpected value: " + timeSpan);
    }
    if (value == null) {
      return Optional.empty();
    }
    return Optional.of(value);
  }

  public Optional<List<MetricEntry<T>>> getMetricEntries(TimeSpan timeSpan) {
    return switch (timeSpan) {
      case WEEK -> Optional.of(mapToMetricEntries(metricEntries_Weekly));
      case MONTH -> Optional.of(mapToMetricEntries(metricEntries_Monthly));
      case ALL_TIME -> Optional.of(mapToMetricEntries(metricEntries_AllTime));
      default -> throw new IllegalStateException("Unexpected value: " + timeSpan);
    };
  }

  protected List<MetricEntry<T>> mapToMetricEntries(
      ConcurrentHashMap<String, BigDecimal> metricEntries) {

    return metricEntries.entrySet().stream()
        .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
        .map(entry -> new MetricEntry<>(entry.getKey(), findByKey(entry.getKey()),
            entry.getValue()))
        .collect(Collectors.toList());
  }

  public record MetricEntry<T>(String key, T entity, BigDecimal value) {

  }

}
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
public abstract class AbstractMetricCollector<T> {

  public static final long MONTH_MILLIS = 1000L * 60 * 60 * 24 * 30;
  public static final long WEEK_MILLIS = 1000L * 60 * 60 * 24 * 7;

  protected static final Map<Class<?>, List<AbstractMetricCollector<?>>> REGISTRY = new ConcurrentHashMap<>();

  public static <T> void initAllCollectors(Class<T> entityType, Collection<T> initialEntries) {
    List<AbstractMetricCollector<?>> metrics = REGISTRY.getOrDefault(entityType, List.of());

    for (AbstractMetricCollector<?> metric : metrics) {
      if (metric.supportsEntityType(entityType)) {
        @SuppressWarnings("unchecked")
        AbstractMetricCollector<T> typedMetric = (AbstractMetricCollector<T>) metric;
        typedMetric.initMetrics(initialEntries);
      }
    }
  }

  public static <T> void updateAllEntriesFor(Class<T> entityType, T entity) {
    List<AbstractMetricCollector<?>> metrics = REGISTRY.getOrDefault(entityType, List.of());

    for (AbstractMetricCollector<?> metric : metrics) {
      if (metric.supportsEntityType(entityType)) {
        @SuppressWarnings("unchecked")
        AbstractMetricCollector<T> typedMetric = (AbstractMetricCollector<T>) metric;
        typedMetric.updateEntry(entity);
      }
    }
  }

  // Static method to remove all entries for a specific entity type
  public static <T> void removeAllEntriesFor(Class<T> entityType, T entity) {
    List<AbstractMetricCollector<?>> metrics = REGISTRY.getOrDefault(entityType, List.of());
    for (AbstractMetricCollector<?> metric : metrics) {
      if (metric.supportsEntityType(entityType)) {
        @SuppressWarnings("unchecked")
        AbstractMetricCollector<T> typedmetric = (AbstractMetricCollector<T>) metric;
        typedmetric.removeEntry(entity);
      }
    }
  }

  // hardcoded to the last 30days is easier to calculate and more consistent over time.
  protected ConcurrentHashMap<String, BigDecimal> metricEntries_Weekly = new ConcurrentHashMap<>();
  protected ConcurrentHashMap<String, BigDecimal> metricEntries_Monthly = new ConcurrentHashMap<>();
  protected ConcurrentHashMap<String, BigDecimal> metricEntries_AllTime = new ConcurrentHashMap<>();

  protected final Class<T> entityType;

  public AbstractMetricCollector(Class<T> entityType) {
    this.entityType = entityType;
    REGISTRY.computeIfAbsent(entityType, k -> new ArrayList<>()).add(this);
  }

  public void initMetrics(Collection<T> initialEntries) {
    metricEntries_Weekly = new ConcurrentHashMap<>();
    metricEntries_Monthly = new ConcurrentHashMap<>();
    metricEntries_AllTime = new ConcurrentHashMap<>();
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
    metricEntries_Weekly.put(getKey(entity), calculateValue(entity, now - WEEK_MILLIS, now));
    metricEntries_Monthly.put(getKey(entity), calculateValue(entity, now - MONTH_MILLIS, now));
    metricEntries_AllTime.put(getKey(entity), calculateValue(entity, 0L, now));
  }

  protected void removeEntry(T entity) {
    metricEntries_Weekly.remove(getKey(entity));
    metricEntries_Monthly.remove(getKey(entity));
    metricEntries_AllTime.remove(getKey(entity));
  }

  public Optional<List<MetricEntry<T>>> getMetricEntries(TimeSpan timeSpan) {
    return switch (timeSpan) {
      case WEEK -> Optional.of(mapToMetricEntries(metricEntries_Weekly));
      case MONTH -> Optional.of(mapToMetricEntries(metricEntries_Monthly));
      case ALL_TIME -> Optional.of(mapToMetricEntries(metricEntries_AllTime));
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
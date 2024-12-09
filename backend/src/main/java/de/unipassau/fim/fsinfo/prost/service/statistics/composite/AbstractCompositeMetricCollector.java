package de.unipassau.fim.fsinfo.prost.service.statistics.composite;

import de.unipassau.fim.fsinfo.prost.data.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.dto.CompositeMetricDTO;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractMetricCollector;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractCompositeMetricCollector<T> extends AbstractMetricCollector<T> {

  public AbstractCompositeMetricCollector(Class<T> entityType) {
    super(entityType);
  }

  public abstract CompositeMetricDTO getCompositeMetricDTO(BigDecimal value, String... keys);

  protected List<CompositeMetricDTO> mapToCompositeMetricEntries(
      ConcurrentHashMap<String, BigDecimal> metricEntries) {

    return metricEntries.entrySet().stream()
        .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
        .map(entry -> getCompositeMetricDTO(entry.getValue(), entry.getKey().split("-")))
        .collect(Collectors.toList());
  }

  public Optional<List<CompositeMetricDTO>> getCompositeMetricEntries(TimeSpan timeSpan) {
    return switch (timeSpan) {
      case WEEK -> Optional.of(mapToCompositeMetricEntries(metricEntries_Weekly));
      case MONTH -> Optional.of(mapToCompositeMetricEntries(metricEntries_Monthly));
      case ALL_TIME -> Optional.of(mapToCompositeMetricEntries(metricEntries_AllTime));
    };
  }

  @Override
  protected T findByKey(String key) {
    return findByKeys(key.split("-"));
  }

  abstract protected T findByKeys(String... keys);

  abstract protected String[] getKeys(T entity);

  @Override
  public String getKey(T entity) {
    String[] keys = getKeys(entity);
    String out = "";
    for (int i = 0; i < keys.length; i++) {
      out += keys[i];
      if (i != keys.length - 1) {
        out += "-";
      }
    }
    return out;
  }
}

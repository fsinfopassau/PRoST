package de.unipassau.fim.fsinfo.prost.service.statistics.user;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.metrics.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.metrics.UserMetricType;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractMetricCollector;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractUserMetricCollector extends AbstractMetricCollector<ProstUser> {

  private final UserRepository userRepository;

  public static final Map<UserMetricType, AbstractUserMetricCollector> COLLECTORS = new HashMap<>();

  public static Optional<List<MetricEntry<ProstUser>>> getMetricEntries(UserMetricType type,
      TimeSpan timeSpan) {
    if (COLLECTORS.containsKey(type)) {
      return COLLECTORS.get(type).getMetricEntries(timeSpan);
    }
    return Optional.empty();
  }

  protected UserMetricType type;

  public AbstractUserMetricCollector(UserMetricType type, UserRepository userRepository) {
    super(ProstUser.class);
    this.userRepository = userRepository;
    this.type = type;
    COLLECTORS.put(type, this);
  }

  @Override
  protected List<MetricEntry<ProstUser>> mapToMetricEntries(
      ConcurrentHashMap<String, BigDecimal> metricEntries) {
    List<MetricEntry<ProstUser>> result = super.mapToMetricEntries(metricEntries);

    List<MetricEntry<ProstUser>> sanitized = new ArrayList<>();

    for (MetricEntry<ProstUser> entry : result) {
      if (!entry.entity().getEnabled()) {
        continue;
      }

      if (entry.entity().getHidden()) {
        sanitized.add(getSanitizedEntry(entry));
      } else {
        sanitized.add(entry);
      }
    }

    return sanitized;
  }

  protected MetricEntry<ProstUser> getSanitizedEntry(MetricEntry<ProstUser> entry) {
    return new MetricEntry<>("", ProstUser.getAnonymous(), entry.value());
  }

  @Override
  public String getKey(ProstUser entity) {
    return entity.getId();
  }

  @Override
  public ProstUser findByKey(String key) {
    Optional<ProstUser> result = userRepository.findById(key);
    return result.orElse(null);
  }

  @Override
  protected boolean filterOut(ProstUser entity) {
    return entity.getHidden();
  }
}

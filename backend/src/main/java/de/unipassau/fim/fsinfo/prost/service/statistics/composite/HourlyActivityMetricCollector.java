package de.unipassau.fim.fsinfo.prost.service.statistics.composite;

import de.unipassau.fim.fsinfo.prost.data.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dto.CompositeMetricDTO;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractMetricCollector;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HourlyActivityMetricCollector extends AbstractMetricCollector<ShopItemHistoryEntry> {

  protected ShopItemHistoryRepository shopItemHistoryRepository;
  protected ShopItemRepository shopItemRepository;
  protected UserRepository userRepository;

  @Autowired
  public HourlyActivityMetricCollector(ShopItemHistoryRepository shopItemHistoryRepository,
      ShopItemRepository shopItemRepository, UserRepository userRepository) {
    super(ShopItemHistoryEntry.class);
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    this.shopItemRepository = shopItemRepository;
    this.userRepository = userRepository;
    initMetrics(shopItemHistoryRepository.findAll());
  }

  @Override
  public BigDecimal calculateValue(ShopItemHistoryEntry entity, Long startTimestamp,
      Long endTimestamp) {
    // Convert entity timestamp to LocalDateTime
    LocalDateTime entityTime = Instant.ofEpochMilli(entity.getTimestamp()).atZone(ZoneOffset.UTC)
        .toLocalDateTime();
    int entityHour = entityTime.getHour(); // Get the hour of the transaction

    // Query the ShopItemHistoryRepository to count transactions made by the user in this timeframe
    List<ShopItemHistoryEntry> entries = shopItemHistoryRepository.findByUserIdAndTimestampBetween(
        entity.getUserId(), startTimestamp, endTimestamp);

    long transactionCount = entries.stream()
        .filter(entry -> {
          LocalDateTime entryTime = Instant.ofEpochMilli(entry.getTimestamp())
              .atZone(ZoneOffset.UTC)
              .toLocalDateTime();
          return entryTime.getHour() == entityHour;
        })
        .count();

    return BigDecimal.valueOf(transactionCount);
  }

  @Override
  public String getKey(ShopItemHistoryEntry entity) {
    LocalDateTime entityTime = Instant.ofEpochMilli(entity.getTimestamp()).atZone(ZoneOffset.UTC)
        .toLocalDateTime();
    int hour = entityTime.getHour();
    return entity.getUserId() + "-" + hour;
  }

  @Override
  public ShopItemHistoryEntry findByKey(String key) {
    String[] parts = key.split("-");
    String userId = parts[0];
    String hour = parts[1];

    return new ShopItemHistoryEntry();
  }

  public CompositeMetricDTO getCompositeMetricDTO(String key1, String key2, BigDecimal value) {

    Optional<ProstUser> userO = userRepository.findById(key1);
    Optional<Integer> hour;

    try {
      hour = Optional.of(Integer.parseInt(key2));
    } catch (NumberFormatException e) {
      hour = Optional.empty();
    }

    ProstUser user = ProstUser.getAnonymous();

    if (userO.isPresent() && !userO.get().getHidden()) {
      user = userO.get();
    }

    return new CompositeMetricDTO(hour.map(Object::toString).orElse("unknown"),
        hour.map(integer -> integer + ":00").orElse("unknown"),
        user.getId(), user.getDisplayName(),
        value);
  }

  protected List<CompositeMetricDTO> mapToCompositeMetricEntries(
      ConcurrentHashMap<String, BigDecimal> metricEntries) {

    return metricEntries.entrySet().stream()
        .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
        .map(entry -> getCompositeMetricDTO(entry.getKey().split("-")[0],
            entry.getKey().split("-")[1], entry.getValue()))
        .collect(Collectors.toList());
  }

  public Optional<List<CompositeMetricDTO>> getCompositeMetricEntries(TimeSpan timeSpan) {
    return switch (timeSpan) {
      case WEEK -> Optional.of(mapToCompositeMetricEntries(metricEntries_Weekly));
      case MONTH -> Optional.of(mapToCompositeMetricEntries(metricEntries_Monthly));
      case ALL_TIME -> Optional.of(mapToCompositeMetricEntries(metricEntries_AllTime));
    };
  }

}

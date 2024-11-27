package de.unipassau.fim.fsinfo.prost.service.statistics.composite;

import de.unipassau.fim.fsinfo.prost.data.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.data.dto.CompositeMetricDTO;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractMetricCollector;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemPurchaseMetricCollector extends AbstractMetricCollector<ShopItemHistoryEntry> {

  protected ShopItemHistoryRepository shopItemHistoryRepository;
  protected ShopItemRepository shopItemRepository;
  protected UserRepository userRepository;

  @Autowired
  public ItemPurchaseMetricCollector(ShopItemHistoryRepository shopItemHistoryRepository,
      ShopItemRepository shopItemRepository, UserRepository userRepository) {
    super(ShopItemHistoryEntry.class);
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    initMetrics(shopItemHistoryRepository.findAll());
    this.shopItemRepository = shopItemRepository;
    this.userRepository = userRepository;
  }

  @Override
  public BigDecimal calculateValue(ShopItemHistoryEntry entity, Long startTimestamp,
      Long endTimestamp) {
    Optional<Long> count = shopItemHistoryRepository.getUsersTotalAmountPurchasedInTimeFrame(
        entity.getUserId(),
        entity.getItemId(), startTimestamp, endTimestamp);
    return count.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
  }

  @Override
  public String getKey(ShopItemHistoryEntry entity) {
    return entity.getItemId() + "-" + entity.getUserId();
  }

  @Override
  public ShopItemHistoryEntry findByKey(String key) {
    String[] parts = key.split("-");
    String itemId = parts[0];
    String userId = parts[1];

    return new ShopItemHistoryEntry(new TransactionEntry(), itemId, BigDecimal.ZERO, 0);
  }

  protected List<CompositeMetricDTO> mapToCompositeMetricEntries(
      ConcurrentHashMap<String, BigDecimal> metricEntries) {

    return metricEntries.entrySet().stream()
        .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
        .map(entry -> getCompositeMetricDTO(entry.getKey().split("-")[0],
            entry.getKey().split("-")[1], entry.getValue()))
        .collect(Collectors.toList());
  }

  public CompositeMetricDTO getCompositeMetricDTO(String itemId, String userId, BigDecimal value) {

    Optional<ShopItem> item = shopItemRepository.findById(itemId);
    Optional<ProstUser> userO = userRepository.findById(userId);
    ProstUser user = ProstUser.getAnonymous();

    if (userO.isPresent() && !userO.get().getHidden()) {
      user = userO.get();
    }

    return new CompositeMetricDTO(itemId, item.isPresent() ? item.get().getDisplayName() : itemId,
        user.getId(), user.getDisplayName(),
        value);
  }

  public Optional<List<CompositeMetricDTO>> getCompositeMetricEntries(TimeSpan timeSpan) {
    return switch (timeSpan) {
      case WEEK -> Optional.of(mapToCompositeMetricEntries(metricEntries_Weekly));
      case MONTH -> Optional.of(mapToCompositeMetricEntries(metricEntries_Monthly));
      case ALL_TIME -> Optional.of(mapToCompositeMetricEntries(metricEntries_AllTime));
    };
  }

}

package de.unipassau.fim.fsinfo.prost.service.statistics.item;

import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.metrics.ItemMetricType;
import de.unipassau.fim.fsinfo.prost.data.metrics.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopSellingItemsMetricCollector extends AbstractItemMetricCollector {

  private final ShopItemHistoryRepository shopItemHistoryRepository;

  @Autowired
  public TopSellingItemsMetricCollector(ShopItemHistoryRepository shopItemHistoryRepository,
      ShopItemRepository shopItemRepository) {
    super(ItemMetricType.TOP_SELLING_ITEMS, shopItemRepository);
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    initMetrics(shopItemHistoryRepository.findAll());
  }

  @Override
  public BigDecimal calculateValue(ShopItemHistoryEntry entity, TimeSpan timeSpan,
      Long startTimeStamp,
      Long endTimeStamp) {
    Optional<Long> result = shopItemHistoryRepository.getTotalAmountPurchasedInTimeFrame(
        entity.getItemId(), startTimeStamp, endTimeStamp);

    return result.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
  }
}

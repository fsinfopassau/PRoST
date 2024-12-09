package de.unipassau.fim.fsinfo.prost.service.statistics.item;

import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.metrics.ItemMetricType;
import de.unipassau.fim.fsinfo.prost.data.metrics.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemRevenueMetricCollector extends AbstractItemMetricCollector {

  private final ShopItemHistoryRepository shopItemHistoryRepository;

  @Autowired
  public ItemRevenueMetricCollector(ShopItemHistoryRepository shopItemHistoryRepository,
      ShopItemRepository shopItemRepository) {
    super(ItemMetricType.ITEM_REVENUE, shopItemRepository);
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    initMetrics(shopItemHistoryRepository.findAll());
  }

  @Override
  public BigDecimal calculateValue(ShopItemHistoryEntry entity, TimeSpan timeSpan,
      Long startTimestamp,
      Long endTimestamp) {

    List<ShopItemHistoryEntry> list = shopItemHistoryRepository.findAllByItemIdAndTimestampBetween(
        entity.getItemId(),
        startTimestamp,
        endTimestamp);

    BigDecimal result = BigDecimal.ZERO;
    for (ShopItemHistoryEntry entry : list) {
      result = result.add(entry.getItemPrice().multiply(BigDecimal.valueOf(entry.getAmount())));
    }

    return result;
  }
}

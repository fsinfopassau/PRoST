package de.unipassau.fim.fsinfo.prost.service.statistics.item;

import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.metrics.ItemMetricType;
import de.unipassau.fim.fsinfo.prost.data.metrics.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractMetricCollector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractItemMetricCollector extends AbstractMetricCollector<ShopItem> {

  protected ShopItemRepository shopItemRepository;

  public static final Map<ItemMetricType, AbstractItemMetricCollector> COLLECTORS = new HashMap<>();

  public static Optional<List<MetricEntry<ShopItem>>> getMetricEntries(ItemMetricType type,
      TimeSpan timeSpan) {
    if (COLLECTORS.containsKey(type)) {
      return COLLECTORS.get(type).getMetricEntries(timeSpan);
    }
    return Optional.empty();
  }

  protected ItemMetricType type;

  public AbstractItemMetricCollector(ItemMetricType type, ShopItemRepository shopItemRepository) {
    super(ShopItem.class);
    this.shopItemRepository = shopItemRepository;
    this.type = type;
    COLLECTORS.put(type, this);
  }

  @Override
  public String getKey(ShopItem entity) {
    return entity.getId();
  }

  @Override
  public ShopItem findByKey(String key) {
    Optional<ShopItem> result = shopItemRepository.findById(key);
    return result.orElse(null);
  }

  @Override
  protected boolean filterOut(ShopItem entity) {
    return false;
  }
}

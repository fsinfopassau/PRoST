package de.unipassau.fim.fsinfo.prost.service.statistics.item;

import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractLeaderboard;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopSellingItemsLeaderboard extends AbstractLeaderboard<ShopItem> {

  private final ShopItemHistoryRepository shopItemHistoryRepository;
  private final ShopItemRepository shopItemRepository;

  @Autowired
  public TopSellingItemsLeaderboard(ShopItemHistoryRepository shopItemHistoryRepository,
      ShopItemRepository shopItemRepository) {
    super(ShopItem.class);
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    initLeaderboard(shopItemRepository.findAll());
    this.shopItemRepository = shopItemRepository;
  }

  @Override
  public BigDecimal calculateValue(ShopItem entity, Long startTimeStamp, Long endTimeStamp) {
    Optional<Long> result = shopItemHistoryRepository.getTotalAmountPurchasedInTimeFrame(
        entity.getId(), startTimeStamp, endTimeStamp);

    System.out.println(
        entity.getId() + " " + result.isPresent() + " " + (result.isPresent() ? result.get() : ""));

    return result.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
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
}

package de.unipassau.fim.fsinfo.prost.service.statistics.user;

import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.metrics.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.metrics.UserMetricType;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LuxuryCustomerMetricCollector extends AbstractUserMetricCollector {

  private final ShopItemHistoryRepository shopItemHistoryRepository;

  @Autowired
  public LuxuryCustomerMetricCollector(
      UserRepository userRepository,
      ShopItemHistoryRepository shopItemHistoryRepository) {
    super(UserMetricType.LUXURY_CUSTOMER, userRepository);
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    initMetrics(shopItemHistoryRepository.findAll());
  }

  @Override
  public BigDecimal calculateValue(ShopItemHistoryEntry entity, TimeSpan timeSpan,
      Long startTimestamp,
      Long endTimestamp) {

    List<ShopItemHistoryEntry> entries = shopItemHistoryRepository.findByUserIdAndTimestampBetween(
        entity.getUserId(), startTimestamp,
        endTimestamp);

    BigDecimal sum = BigDecimal.ZERO;
    int count = 0;

    for (ShopItemHistoryEntry entry : entries) {
      count += entry.getAmount();
      sum = sum.add(entry.getItemPrice().multiply(BigDecimal.valueOf(entry.getAmount())));
    }

    if (count != 0) {
      return sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.FLOOR);
    }
    return BigDecimal.ZERO;
  }
}

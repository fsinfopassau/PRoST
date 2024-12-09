package de.unipassau.fim.fsinfo.prost.service.statistics.user;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.metrics.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.metrics.UserMetricType;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Most transactions per user
@Service
public class LoyalCustomerMetricCollector extends AbstractUserMetricCollector {

  private final ShopItemHistoryRepository shopItemHistoryRepository;

  @Autowired
  public LoyalCustomerMetricCollector(UserRepository userRepository,
      ShopItemHistoryRepository shopItemHistoryRepository) {
    super(UserMetricType.LOYAL_CUSTOMER, userRepository);
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    initMetrics(userRepository.findAll());
  }

  @Override
  public BigDecimal calculateValue(ProstUser entity, TimeSpan timeSpan, Long startTimestamp,
      Long endTimestamp) {
    return BigDecimal.valueOf(
        shopItemHistoryRepository.findByUserIdAndTimestampBetween(entity.getId(), startTimestamp,
            endTimestamp).size());
  }
}

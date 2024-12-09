package de.unipassau.fim.fsinfo.prost.service.statistics.user;

import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.metrics.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.metrics.UserMetricType;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KioskBuyersMetricCollector extends AbstractUserMetricCollector {

  private final ShopItemHistoryRepository shopItemHistoryRepository;

  @Autowired
  public KioskBuyersMetricCollector(UserRepository userRepository,
      ShopItemHistoryRepository shopItemHistoryRepository) {
    super(UserMetricType.KIOSK_CUSTOMER, userRepository);
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    initMetrics(shopItemHistoryRepository.findAll());
  }

  @Override
  public BigDecimal calculateValue(ShopItemHistoryEntry entity, TimeSpan timeSpan,
      Long startTimestamp,
      Long endTimestamp) {

    List<ShopItemHistoryEntry> items = shopItemHistoryRepository.findByUserIdAndTimestampBetween(
        entity.getUserId(), startTimestamp,
        endTimestamp);

    // We only check for transactions from other users, because only the kiosk and admins have
    // authority to make transactions for other users.
    Stream<ShopItemHistoryEntry> filter = items.stream()
        .filter(entry -> !entry.getTransaction().getBearerId().equals(entity.getUserId()));

    return BigDecimal.valueOf(filter.count());
  }
}

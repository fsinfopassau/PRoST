package de.unipassau.fim.fsinfo.prost.service.statistics.user;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractLeaderboard;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Most transactions per user
@Service
public class LoyalCustomerLeaderboard extends AbstractLeaderboard<ProstUser> {

  private final ShopItemHistoryRepository shopItemHistoryRepository;

  @Autowired
  public LoyalCustomerLeaderboard(UserRepository userRepository,
      ShopItemHistoryRepository shopItemHistoryRepository) {
    super(ProstUser.class);
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    initLeaderboard(userRepository.findAll());
  }

  @Override
  public BigDecimal calculateValue(ProstUser entity, Long startTimestamp, Long endTimestamp) {
    return BigDecimal.valueOf(
        shopItemHistoryRepository.findByUserIdAndTimestampBetween(entity.getId(), startTimestamp,
            endTimestamp).size());
  }
}

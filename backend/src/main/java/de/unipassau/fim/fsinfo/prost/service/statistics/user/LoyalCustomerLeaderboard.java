package de.unipassau.fim.fsinfo.prost.service.statistics.user;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractUserLeaderboard;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Most transactions per user
@Service
public class LoyalCustomerLeaderboard extends AbstractUserLeaderboard {

  private final ShopItemHistoryRepository shopItemHistoryRepository;
  private final UserRepository userRepository;

  @Autowired
  public LoyalCustomerLeaderboard(UserRepository userRepository,
      ShopItemHistoryRepository shopItemHistoryRepository) {
    super();
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    initLeaderboard(userRepository.findAll());
    this.userRepository = userRepository;
  }

  @Override
  public BigDecimal calculateValue(ProstUser entity, Long startTimestamp, Long endTimestamp) {
    return BigDecimal.valueOf(
        shopItemHistoryRepository.findByUserIdAndTimestampBetween(entity.getId(), startTimestamp,
            endTimestamp).size());
  }

  @Override
  public String getKey(ProstUser entity) {
    return entity.getId();
  }

  @Override
  public ProstUser findByKey(String key) {
    Optional<ProstUser> result = userRepository.findById(key);
    return result.orElse(null);
  }
}

package de.unipassau.fim.fsinfo.prost.service.statistics.composite;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.data.dto.CompositeMetricDTO;
import de.unipassau.fim.fsinfo.prost.data.metrics.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemPurchaseMetricCollector extends
    AbstractCompositeMetricCollector<ShopItemHistoryEntry> {

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
  public BigDecimal calculateValue(ShopItemHistoryEntry entity, TimeSpan timeSpan,
      Long startTimestamp, Long endTimestamp) {

    Optional<BigDecimal> valueO = getValue(timeSpan, getKey(entity));

    if (entity.getTimestamp() >= startTimestamp && entity.getTimestamp() <= endTimestamp) {
      return valueO.map(bigDecimal -> bigDecimal.add(BigDecimal.valueOf(entity.getAmount())))
          .orElseGet(() -> BigDecimal.valueOf(entity.getAmount()));
    }

    return valueO.orElse(BigDecimal.ZERO);
  }

  @Override
  public String[] getKeys(ShopItemHistoryEntry entity) {
    LocalDateTime entityTime = Instant.ofEpochMilli(entity.getTimestamp()).atZone(ZoneOffset.UTC)
        .toLocalDateTime();
    int hour = entityTime.getHour();
    return new String[]{entity.getItemId(), entity.getUserId()};
  }

  @Override
  public ShopItemHistoryEntry findByKeys(String... keys) {
    return new ShopItemHistoryEntry(new TransactionEntry(), keys[0], BigDecimal.ZERO, 0);
  }

  @Override
  public CompositeMetricDTO getCompositeMetricDTO(BigDecimal value, String... keys) {
    String itemId = keys[0];
    String userId = keys[1];

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

}

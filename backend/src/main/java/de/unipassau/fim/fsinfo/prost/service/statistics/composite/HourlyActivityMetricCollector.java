package de.unipassau.fim.fsinfo.prost.service.statistics.composite;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dto.CompositeMetricDTO;
import de.unipassau.fim.fsinfo.prost.data.metrics.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HourlyActivityMetricCollector extends
    AbstractCompositeMetricCollector<ShopItemHistoryEntry> {

  protected ShopItemHistoryRepository shopItemHistoryRepository;
  protected ShopItemRepository shopItemRepository;
  protected UserRepository userRepository;

  @Value("${ZONE_ID:Europe/Berlin}")
  private String zoneId;
  private ZoneId zone;

  @Autowired
  public HourlyActivityMetricCollector(ShopItemHistoryRepository shopItemHistoryRepository,
      ShopItemRepository shopItemRepository, UserRepository userRepository) {
    super(ShopItemHistoryEntry.class);
    this.shopItemHistoryRepository = shopItemHistoryRepository;
    this.shopItemRepository = shopItemRepository;
    this.userRepository = userRepository;
  }

  @PostConstruct
  public void init() {
    if (zoneId != null) {
      zone = ZoneId.of(zoneId);
      System.out.println("[HAMC] :: zone : " + zoneId);
    } else {
      zone = ZoneOffset.UTC;
      System.out.println("[HAMC] :: zone defaults to UTC");
    }
    initMetrics(shopItemHistoryRepository.findAll());
  }

  @Override
  public BigDecimal calculateValue(ShopItemHistoryEntry entity, TimeSpan timeSpan,
      Long startTimestamp,
      Long endTimestamp) {
    Optional<BigDecimal> valueO = getValue(timeSpan, getKey(entity));

    if (entity.getTimestamp() >= startTimestamp && entity.getTimestamp() <= endTimestamp) {
      return valueO.map(bigDecimal -> bigDecimal.add(BigDecimal.ONE)).orElse(BigDecimal.ONE);
    }

    return valueO.orElse(BigDecimal.ZERO);
  }

  @Override
  protected boolean filterOut(ShopItemHistoryEntry entity) {
    Optional<ProstUser> userO = userRepository.findById(entity.getUserId());
    if (userO.isPresent()) {
      ProstUser user = userO.get();
      return user.getHidden();
    }
    return false;
  }

  @Override
  public String[] getKeys(ShopItemHistoryEntry entity) {
    ZoneOffset offset = zone.getRules().getOffset(Instant.now());
    LocalDateTime entityTime = Instant.ofEpochMilli(entity.getTimestamp()).atZone(offset)
        .toLocalDateTime();
    int hour = entityTime.getHour();
    return new String[]{entity.getUserId(), "" + hour};
  }

  @Override
  public ShopItemHistoryEntry findByKeys(String... keys) {
    return new ShopItemHistoryEntry();
  }

  @Override
  public CompositeMetricDTO getCompositeMetricDTO(BigDecimal value, String... keys) {
    String key1 = keys[0];
    String key2 = keys[1];

    Optional<ProstUser> userO = userRepository.findById(key1);
    Optional<Integer> hour;

    try {
      hour = Optional.of(Integer.parseInt(key2));
    } catch (NumberFormatException e) {
      hour = Optional.empty();
    }

    ProstUser user = ProstUser.getAnonymous();

    if (userO.isPresent() && !userO.get().getHidden()) {
      user = userO.get();
    }

    return new CompositeMetricDTO(hour.map(Object::toString).orElse("unknown"),
        hour.map(integer -> integer + ":00").orElse("unknown"),
        user.getId(), user.getDisplayName(),
        value);
  }

}

package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.DataFilter;
import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import de.unipassau.fim.fsinfo.prost.data.UserAccessRole;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractMetricCollector;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopService {

  final private ShopItemRepository itemRepository;
  final private ShopItemHistoryRepository historyRepository;
  final private UserRepository userRepository;

  final private TransactionService transactionService;

  final static BigDecimal MAX_PRICE = new BigDecimal(100);
  final static BigDecimal MIN_PRICE = BigDecimal.ZERO;

  @Value("${BUY_COOLDOWN:10000}")
  Long buyCooldownTime;

  /**
   * Authorized User-ID, Unix-Timestamp
   */
  final private HashMap<String, Long> bearerLastBuy = new HashMap<>();

  @Autowired
  public ShopService(ShopItemRepository itemRepository, ShopItemHistoryRepository historyRepository,
      UserRepository userRepository, TransactionService transactionService) {
    this.itemRepository = itemRepository;
    this.historyRepository = historyRepository;
    this.userRepository = userRepository;
    this.transactionService = transactionService;
  }

  public boolean hasBearerCooldown(String userId) {
    if (bearerLastBuy.containsKey(userId)) {
      Long lastTime = bearerLastBuy.get(userId);
      return lastTime + buyCooldownTime > Instant.now().toEpochMilli();
    }
    return false;
  }

  // Because checking access-rights before trying to buy determines the error-messages to the client.
  public boolean hasBearerPermissions(String itemId, String userId, int amount, String bearerId,
      UserAccessRole bearerRole) {
    Optional<ProstUser> userO = userRepository.findById(userId);

    switch (bearerRole) {
      case KAFFEEKASSE -> {
        return true;
      }
      case KIOSK -> {
        return userO.isPresent() && userO.get().getKiosk();
      }
      case FSINFO -> {
        return bearerId != null && bearerId.equals(userId);
      }
    }
    return false;
  }

  @Transactional
  public boolean consume(String itemId, String userId, int amount, String bearerId,
      UserAccessRole bearerRole) {
    Optional<ShopItem> itemO = itemRepository.findById(itemId);
    Optional<ProstUser> userO = userRepository.findById(userId);
    Optional<ProstUser> bearerUser = userRepository.findById(bearerId);

    if (amount < 1 || amount > 10) {
      System.out.println("[SS] :: invalid amount=" + amount);
      return false;
    }

    if (!hasBearerPermissions(itemId, userId, amount, bearerId, bearerRole)) {
      System.out.println(
          "[SS] :: " + bearerId + " does not have permissions for buyprocess for " + userId);
      return false;
    }

    if (hasBearerCooldown(bearerId)) {
      System.out.println("[SS] :: " + userO + " on Cooldown!");
      return false;
    }

    if (userO.isEmpty() || itemO.isEmpty() || bearerUser.isEmpty()) {
      System.out.println("[SS] :: empty :: " + userO + " " + itemO + " " + bearerUser);
      return false;
    }

    ProstUser user = userO.get();
    ProstUser bearer = bearerUser.get();
    ShopItem item = itemO.get();

    // Every Component needs to be allowed to be part of the Transaction
    if (!(item.getEnabled() && user.getEnabled() && bearer.getEnabled())) {
      System.out.println(
          "[SS] :: Not Enabled :: item=" + item.getEnabled() + " user=" + user.getEnabled()
              + " bearer="
              + bearer.getEnabled());
      return false;
    }

    Optional<TransactionEntry> transaction = transactionService.moneyTransfer(
        Optional.empty(), user.getId(), bearer.getId(),
        item.getPrice().abs().multiply(BigDecimal.valueOf(amount).abs()), TransactionType.BUY);

    if (transaction.isPresent()) {
      ShopItemHistoryEntry historyEntry = new ShopItemHistoryEntry(transaction.get(), item.getId(),
          item.getPrice(),
          amount);
      historyRepository.save(historyEntry);
      bearerLastBuy.put(bearerId, Instant.now().toEpochMilli());

      if (!user.getHidden()) {
        AbstractMetricCollector.updateAllEntriesFor(ProstUser.class, user);
        AbstractMetricCollector.updateAllEntriesFor(ShopItem.class, item);
        AbstractMetricCollector.updateAllEntriesFor(ShopItemHistoryEntry.class, historyEntry);
      }
      return true;
    } else {
      System.out.println("[SS] :: No Transaction found!");
      return false;
    }
  }

  @Transactional
  public Optional<ShopItem> createItem(String identifier, String displayName, String category,
      BigDecimal price) {

    if (!DataFilter.isValidString(displayName, "display name")
        || !DataFilter.isValidString(category, "category")
        || !DataFilter.isValidString(identifier, "identifier")) {
      return Optional.empty();
    }

    if (itemRepository.existsById(identifier)) {
      System.out.println("[SS] :: identifier \"" + identifier + "\" already present.");
      return Optional.empty();
    }

    if (!DataFilter.isValidMoney(price)) {
      System.out.println("[SS] :: The price is invalid or null");
      return Optional.empty();
    }

    ShopItem item = new ShopItem(DataFilter.filterNameId(identifier), category, displayName,
        price.abs());
    itemRepository.save(item);
    AbstractMetricCollector.updateAllEntriesFor(ShopItem.class, item);
    return Optional.of(item);
  }

  @Transactional
  public Optional<ShopItem> delete(String identifier) {
    Optional<ShopItem> item = itemRepository.findById(identifier);
    if (item.isPresent()) {
      itemRepository.delete(item.get());
      AbstractMetricCollector.removeAllEntriesFor(ShopItem.class, item.get());
      return item;
    }
    return Optional.empty();
  }

  @Transactional
  public Optional<ShopItem> changeDisplayName(String identifier, String newDisplayName) {
    Optional<ShopItem> item = itemRepository.findById(identifier);

    if (!DataFilter.isValidString(newDisplayName, "display name")) {
      return Optional.empty();
    }

    if (item.isPresent()) {
      item.get().setDisplayName(newDisplayName);
      itemRepository.save(item.get());
      return item;
    }
    return Optional.empty();
  }

  @Transactional
  public Optional<ShopItem> changeCategory(String identifier, String category) {
    Optional<ShopItem> item = itemRepository.findById(identifier);

    if (!DataFilter.isValidString(category, "category")) {
      return Optional.empty();
    }

    if (item.isPresent()) {
      item.get().setCategory(category);
      itemRepository.save(item.get());
      return item;
    }
    return Optional.empty();
  }

  @Transactional
  public Optional<ShopItem> changePrice(String identifier, String value) {
    Optional<ShopItem> item = itemRepository.findById(identifier);

    try {
      if (item.isPresent()) {
        BigDecimal price = new BigDecimal(value);

        if (!DataFilter.isValidMoney(price)) {
          System.out.println("[SS] :: Price-value with " + price + " not valid!");
          return Optional.empty();
        }

        if (price.compareTo(MAX_PRICE) > 0) {
          System.out.println("[SS] :: Price is with " + price + " too high!");
          return Optional.empty();
        } else if (price.compareTo(MIN_PRICE) < 0) {
          System.out.println("[SS] :: Price is with " + price + " too low!");
          return Optional.empty();
        }

        item.get().setPrice(price);
        itemRepository.save(item.get());
        return item;
      }
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
    return Optional.empty();
  }

  @Transactional
  public Optional<ShopItem> enable(String identifier) {
    Optional<ShopItem> item = setVisibility(identifier, true);
    item.ifPresent(
        shopItem -> AbstractMetricCollector.updateAllEntriesFor(ShopItem.class, shopItem));
    return item;
  }

  @Transactional
  public Optional<ShopItem> disable(String identifier) {
    Optional<ShopItem> item = setVisibility(identifier, false);
    item.ifPresent(
        shopItem -> AbstractMetricCollector.removeAllEntriesFor(ShopItem.class, shopItem));
    return item;
  }

  private Optional<ShopItem> setVisibility(String identifier, boolean value) {
    Optional<ShopItem> item = itemRepository.findById(identifier);
    if (item.isPresent()) {
      item.get().setEnabled(value);
      itemRepository.save(item.get());
    }
    return item;
  }

}

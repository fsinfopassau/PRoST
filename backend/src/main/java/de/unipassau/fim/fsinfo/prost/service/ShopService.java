package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.DataFilter;
import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
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

  @Transactional
  public boolean consume(String itemId, String userId, int amount, String bearerId) {
    Optional<ShopItem> itemO = itemRepository.findById(itemId);
    Optional<ProstUser> userO = userRepository.findById(userId);
    Optional<ProstUser> bearerUser = userRepository.findById(bearerId);

    if (amount < 1 || amount > 10) {
      return false;
    }

    if (hasBearerCooldown(bearerId)) {
      return false;
    }

    if (userO.isEmpty() || itemO.isEmpty() || bearerUser.isEmpty()) {
      System.out.println(userO + " " + itemO + " " + bearerUser);
      return false;
    }

    ProstUser user = userO.get();
    ProstUser bearer = bearerUser.get();
    ShopItem item = itemO.get();

    // Every Component needs to be allowed to be part of the Transaction
    if (!item.getEnabled() || !user.getEnabled()) {
      return false;
    }

    Optional<TransactionEntry> transaction = transactionService.moneyTransfer(
        Optional.empty(), user.getId(), bearer.getId(),
        item.getPrice().abs().multiply(BigDecimal.valueOf(amount).abs()), TransactionType.BUY);

    if (transaction.isPresent()) {
      historyRepository.save(
          new ShopItemHistoryEntry(transaction.get(), item.getId(), item.getPrice(),
              amount));
      bearerLastBuy.put(bearerId, Instant.now().toEpochMilli());
      return true;
    } else {
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
    return Optional.of(item);
  }

  @Transactional
  public Optional<ShopItem> delete(String identifier) {
    Optional<ShopItem> item = itemRepository.findById(identifier);
    if (item.isPresent()) {
      itemRepository.delete(item.get());
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
    return setVisibility(identifier, true);
  }

  @Transactional
  public Optional<ShopItem> disable(String identifier) {
    return setVisibility(identifier, false);
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

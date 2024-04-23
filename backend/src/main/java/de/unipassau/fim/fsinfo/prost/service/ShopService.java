package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopService {

  final ShopItemRepository itemRepository;
  final ShopItemHistoryRepository historyRepository;
  final UserRepository userRepository;

  final TransactionService transactionService;

  final static int nameLength = 20;
  final static BigDecimal maxPrice = new BigDecimal(100);

  @Autowired
  public ShopService(ShopItemRepository itemRepository, ShopItemHistoryRepository historyRepository,
      UserRepository userRepository, TransactionService transactionService) {
    this.itemRepository = itemRepository;
    this.historyRepository = historyRepository;
    this.userRepository = userRepository;
    this.transactionService = transactionService;
  }

  @Transactional
  public boolean consume(String itemId, String userId, int amount, String bearerId) {
    Optional<ShopItem> itemO = itemRepository.findById(itemId);
    Optional<ProstUser> userO = userRepository.findById(userId);
    Optional<ProstUser> bearerUser = userRepository.findById(bearerId);

    if (userO.isEmpty() || itemO.isEmpty() || bearerUser.isEmpty()) {
      System.out.println(userO + " " + itemO + " " + bearerUser);
      return false;
    }

    ProstUser user = userO.get();
    ProstUser bearer = bearerUser.get();
    ShopItem item = itemO.get();

    if (!item.getEnabled() || !user.getEnabled() || !bearer.getEnabled()) {
      return false;
    }

    userRepository.save(user);

    Optional<TransactionEntry> transaction = transactionService.moneyTransfer(
        Optional.empty(), user.getId(), bearer.getId(),
        item.getPrice().multiply(BigDecimal.valueOf(amount)), TransactionType.BUY);

    if (transaction.isPresent()) {
      historyRepository.save(
          new ShopItemHistoryEntry(transaction.get(), item.getId(), item.getPrice(),
              amount));
      return true;
    } else {
      return false;
    }
  }

  @Transactional
  public Optional<ShopItem> createItem(String identifier, String displayName, String category, BigDecimal price ){

    if (checkEmpty(displayName,"display name")
            || checkEmpty(category,"category")
            || checkEmpty(identifier,"identifier")
            || checkSize(displayName, "display name")
            || checkSize(category, "category")
            || checkSize(identifier, "identifier")) {
      return Optional.empty();
    }

    if (itemRepository.existsById(identifier)) {
      System.out.println("[SS] :: identifier \"" + identifier + "\" already present.");
      return Optional.empty();
    }

    if (price == null) {
      System.out.println("[SS] :: The price is invalid or null");
      return Optional.empty();
    }

    ShopItem item = new ShopItem(identifier,category,displayName,price.abs());
    itemRepository.save(item);
    return Optional.of(item);
  }

  @Transactional
  public Optional<ShopItem> delete(String identifier) {
    Optional<ShopItem> item = itemRepository.findById(identifier);
    if(item.isPresent()) {
      itemRepository.delete(item.get());
      return item;
    }
    return Optional.empty();
  }

  @Transactional
  public Optional<ShopItem> changeDisplayName(String identifier, String newDisplayName) {
    Optional<ShopItem> item = itemRepository.findById(identifier);

    if (checkEmpty(newDisplayName, "display name") || checkSize(newDisplayName, "display name")) {
      return Optional.empty();
    }

    if(item.isPresent()) {
      item.get().setDisplayName(newDisplayName);
      itemRepository.save(item.get());
      return item;
    }
    return Optional.empty();
  }

  @Transactional
  public Optional<ShopItem> changeCategory(String identifier, String category) {
    Optional<ShopItem> item = itemRepository.findById(identifier);

    if (checkEmpty(category, "category") || checkSize(category, "category")) {
      return Optional.empty();
    }

    if(item.isPresent()) {
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
        if(price.compareTo(maxPrice) > 0) {
          System.out.println("[SS] :: Price is with " + price + " to high");
          return Optional.empty();
        }
        item.get().setPrice(price.abs());
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

  private boolean checkEmpty(String value, String name) {
    if(value == null || value.isBlank() ) {
      System.out.println("[SS] :: " + name +" is empty");
      return true;
    }
    return false;
  }

  private boolean checkSize(String value, String name) {
    if(value.length() > nameLength ) {
      System.out.println("[SS] :: " + name + " size to large");
      return true;
    }
    return false;
  }

}

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopService {

  final ShopItemRepository itemRepository;
  final ShopItemHistoryRepository historyRepository;
  final UserRepository userRepository;

  final TransactionService transactionService;

  final int nameLength = 20;

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

    if (identifier == null || identifier.isBlank()) {
      System.out.println("[SS] :: identifier is empty.");
      return Optional.empty();
    }

    if (itemRepository.existsById(identifier)) {
      System.out.println("[SS] :: identifier \"" + identifier + "\" already present.");
      return Optional.empty();
    }

    if (displayName == null || displayName.isBlank()) {
      System.out.println("[SS] :: display name is empty.");
      return Optional.empty();
    }

    if (category == null || category.isBlank()) {
      System.out.println("[SS] :: category is empty");
      return Optional.empty();
    }

    if (displayName.length() > nameLength || category.length() > nameLength || identifier.length() > nameLength) {
      System.out.println("[SS] :: string size to large");
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

}

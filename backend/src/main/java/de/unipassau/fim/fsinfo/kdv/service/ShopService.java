package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopService {

  final
  ShopItemRepository itemRepository;
  final
  ShopItemHistoryRepository historyRepository;
  final
  UserRepository userRepository;

  @Autowired
  public ShopService(ShopItemRepository itemRepository, ShopItemHistoryRepository historyRepository,
      UserRepository userRepository) {
    this.itemRepository = itemRepository;
    this.historyRepository = historyRepository;
    this.userRepository = userRepository;
  }

  public boolean consume(String itemId, String userId, int amount) {
    Optional<ShopItem> itemO = itemRepository.findById(itemId);
    Optional<KdvUser> userO = userRepository.findById(userId);

    if (userO.isEmpty() || itemO.isEmpty()) {
      return false;
    }

    KdvUser user = userO.get();
    ShopItem item = itemO.get();

    if (!item.getEnabled() || !user.getEnabled()) {
      return false;
    }

    user.setBalance(
        user.getBalance().subtract(item.getPrice().multiply(BigDecimal.valueOf(amount))));

    userRepository.save(user);
    historyRepository.save(
        new ShopItemHistoryEntry(user.getId(), item.getId(), item.getPrice(), amount));

    return true;
  }

}

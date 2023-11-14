package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopHistory;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.data.dao.User;
import de.unipassau.fim.fsinfo.kdv.data.dto.ConsumeDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopHistoryRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

  @Autowired
  ShopItemRepository itemRepository;
  @Autowired
  ShopHistoryRepository historyRepository;
  @Autowired
  UserRepository userRepository;

  @GetMapping("/item")
  public ResponseEntity<List<ShopItem>> list() {
    return ResponseEntity.ok(itemRepository.findAll());
  }

  @PostMapping("/item/consume")
  public ResponseEntity<String> consume(@RequestBody ConsumeDTO consumeDTO) {
    Optional<User> userOption = userRepository.findById(consumeDTO.getUserId());
    Optional<ShopItem> itemOptional = itemRepository.findById(consumeDTO.getItemId());

    if (userOption.isPresent() && itemOptional.isPresent()) {

      User user = userOption.get();
      ShopItem item = itemOptional.get();

      user.setBalance(user.getBalance() - item.getPrice());
      userRepository.save(user);

      ShopHistory history = new ShopHistory(user.getId(), item.getId(), item.getPrice());
      historyRepository.save(history);

      return ResponseEntity.ok(user.getBalance() + "");
    }
    return ResponseEntity.ok(consumeDTO.getUserId() + " " + consumeDTO.getItemId());
  }

  @GetMapping("/history")
  public ResponseEntity<List<ShopHistory>> history() {
    return ResponseEntity.ok(historyRepository.findAll());
  }

  @GetMapping("/history/{userId}")
  public ResponseEntity<String> historyUser(@PathVariable String userId) {
    // TODO
    return ResponseEntity.noContent().build();
  }

}

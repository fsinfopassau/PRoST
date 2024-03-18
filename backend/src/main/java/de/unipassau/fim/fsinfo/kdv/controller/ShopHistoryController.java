package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.dto.ShopItemHistoryEntryDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.security.CustomUserDetailsContextMapper.CustomUserDetails;
import de.unipassau.fim.fsinfo.kdv.service.ShopHistoryService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class ShopHistoryController {

  private final ShopHistoryService shopHistoryService;
  private final UserRepository userRepository;

  @Autowired
  public ShopHistoryController(ShopHistoryService shopHistoryService,
      UserRepository userRepository) {
    this.shopHistoryService = shopHistoryService;
    this.userRepository = userRepository;
  }

  @GetMapping("/last")
  public ResponseEntity<List<ShopItemHistoryEntryDTO>> history(
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) Integer n) {
    if (userId == null) {
      return ResponseEntity.ok(shopHistoryService.getLastHistory(n));
    } else {
      return ResponseEntity.ok(shopHistoryService.getLastUserHistory(n, userId));
    }
  }

  @GetMapping("/me")
  public ResponseEntity<List<ShopItemHistoryEntryDTO>> historyMe(
      @RequestParam(required = false) Integer n, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<KdvUser> user = userRepository.findById(userDetails.getUsername());
    if (user.isPresent()) {
      return ResponseEntity.ok(shopHistoryService.getLastUserHistory(n, userDetails.getUsername()));
    }
    return ResponseEntity.notFound().build();

  }
}

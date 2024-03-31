package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dto.ShopItemHistoryEntryDTO;
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
@RequestMapping("/api/history/shop")
public class ShopHistoryController {

  private final ShopHistoryService shopHistoryService;

  @Autowired
  public ShopHistoryController(ShopHistoryService shopHistoryService) {
    this.shopHistoryService = shopHistoryService;
  }

  @GetMapping("/last")
  public ResponseEntity<List<ShopItemHistoryEntryDTO>> history(
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) Integer n) {
    if (userId == null) {
      return ResponseEntity.ok(shopHistoryService.getLastHistory(n));
    } else {
      Optional<List<ShopItemHistoryEntryDTO>> list = shopHistoryService.getLastUserHistory(n,
          userId);

      return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
  }

  @GetMapping("/me")
  public ResponseEntity<List<ShopItemHistoryEntryDTO>> historyMe(
      @RequestParam(required = false) Integer n, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<List<ShopItemHistoryEntryDTO>> list = shopHistoryService.getLastUserHistory(n,
        userDetails.getUsername());

    return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

  }
}

package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.dto.ShopItemHistoryEntryDTO;
import de.unipassau.fim.fsinfo.prost.security.CustomUserDetailsContextMapper.CustomUserDetails;
import de.unipassau.fim.fsinfo.prost.service.ShopHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

  /**
   * @param p: Page-Number 0-MAX
   * @param s: Page-Size 1-MAX
   * @return Page + infos
   */
  @GetMapping("/list")
  public Page<ShopItemHistoryEntryDTO> getTransactions(
      @RequestParam(defaultValue = "0") int p,
      @RequestParam(defaultValue = "10") int s,
      @RequestParam(required = false) String receiverId) {
    return shopHistoryService.getHistory(Math.max(0, p), Math.max(1, s), receiverId);
  }

  /**
   * @param p: Page-Number 0-MAX
   * @param s: Page-Size 1-MAX
   * @return Page + infos
   */
  @GetMapping("/me")
  public Page<ShopItemHistoryEntryDTO> getPersonalTransactions(
      @RequestParam(defaultValue = "0") int p,
      @RequestParam(defaultValue = "10") int s,
      Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return shopHistoryService.getHistory(Math.max(0, p), Math.max(1, s),
        userDetails.getUsername());
  }
}

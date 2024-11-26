package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.ItemLeaderboardType;
import de.unipassau.fim.fsinfo.prost.data.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.UserLeaderboardType;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractLeaderboard.LeaderboardEntry;
import de.unipassau.fim.fsinfo.prost.service.statistics.item.TopSellingItemsLeaderboard;
import de.unipassau.fim.fsinfo.prost.service.statistics.user.LoyalCustomerLeaderboard;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

  private final TopSellingItemsLeaderboard topSellingItemsLeaderboard;
  private final LoyalCustomerLeaderboard loyalCustomerLeaderboard;

  @Autowired
  public StatisticsController(TopSellingItemsLeaderboard topSellingItemsLeaderboard,
      LoyalCustomerLeaderboard loyalCustomerLeaderboard) {
    this.topSellingItemsLeaderboard = topSellingItemsLeaderboard;
    this.loyalCustomerLeaderboard = loyalCustomerLeaderboard;
  }

  @GetMapping("/item/leaderboard")
  public ResponseEntity<List<LeaderboardEntry<ShopItem>>> getItemLeaderboard(
      ItemLeaderboardType type, TimeSpan timespan) {
    switch (type) {
      case TOP_SELLING_ITEMS -> {
        return topSellingItemsLeaderboard.getLeaderboardEntries(timespan).map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.badRequest().build());
      }
    }
    return ResponseEntity.badRequest().build();
  }

  @GetMapping("/user/leaderboard")
  public ResponseEntity<List<LeaderboardEntry<ProstUser>>> getUserLeaderboard(
      UserLeaderboardType type, TimeSpan timespan) {
    switch (type) {
      case LOYAL_CUSTOMER -> {
        return loyalCustomerLeaderboard.getLeaderboardEntries(timespan).map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.badRequest().build());
      }
    }
    return ResponseEntity.badRequest().build();
  }
}
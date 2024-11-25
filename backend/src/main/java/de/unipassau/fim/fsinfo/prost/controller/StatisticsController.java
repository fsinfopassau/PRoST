package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.LeaderboardType;
import de.unipassau.fim.fsinfo.prost.data.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractLeaderboard.LeaderboardEntry;
import de.unipassau.fim.fsinfo.prost.service.statistics.TopSellingItemsLeaderboard;
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

  @Autowired
  public StatisticsController(TopSellingItemsLeaderboard topSellingItemsLeaderboard) {
    this.topSellingItemsLeaderboard = topSellingItemsLeaderboard;
  }

  @GetMapping("/leaderboard")
  public ResponseEntity<List<LeaderboardEntry<ShopItem>>> getMonthlyLeaderboard(
      LeaderboardType type, TimeSpan timespan) {
    switch (type) {
      case TopSellingItems -> {
        return topSellingItemsLeaderboard.getLeaderboardEntries(timespan).map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.badRequest().build());
      }
    }
    return ResponseEntity.badRequest().build();
  }
}
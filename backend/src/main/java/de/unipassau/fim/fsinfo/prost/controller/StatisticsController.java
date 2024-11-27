package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.ItemMetricType;
import de.unipassau.fim.fsinfo.prost.data.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.UserMetricType;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractItemMetricCollector;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractMetricCollector.MetricEntry;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractUserMetricCollector;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

  @GetMapping("/item/leaderboard")
  public ResponseEntity<List<MetricEntry<ShopItem>>> getItemLeaderboard(
      ItemMetricType type, TimeSpan timespan) {
    return AbstractItemMetricCollector.getMetricEntries(type, timespan).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @GetMapping("/user/leaderboard")
  public ResponseEntity<List<MetricEntry<ProstUser>>> getUserLeaderboard(
      UserMetricType type, TimeSpan timespan) {
    return AbstractUserMetricCollector.getMetricEntries(type, timespan).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }
}
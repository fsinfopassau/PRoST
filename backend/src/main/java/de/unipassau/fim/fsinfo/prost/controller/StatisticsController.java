package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.CompositeMetricType;
import de.unipassau.fim.fsinfo.prost.data.ItemMetricType;
import de.unipassau.fim.fsinfo.prost.data.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.UserMetricType;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dto.CompositeMetricDTO;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractMetricCollector.MetricEntry;
import de.unipassau.fim.fsinfo.prost.service.statistics.composite.ItemPurchaseMetricCollector;
import de.unipassau.fim.fsinfo.prost.service.statistics.item.AbstractItemMetricCollector;
import de.unipassau.fim.fsinfo.prost.service.statistics.user.AbstractUserMetricCollector;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

  private final ItemPurchaseMetricCollector metricCollector;

  @Autowired
  public StatisticsController(ItemPurchaseMetricCollector metricCollector) {
    this.metricCollector = metricCollector;
  }

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

  @GetMapping("/composite/leaderboard")
  public ResponseEntity<List<CompositeMetricDTO>> getCompositeLeaderboard(
      CompositeMetricType type, TimeSpan timespan) {
    if (type == CompositeMetricType.ITEM_USER) {
      return metricCollector.getCompositeMetricEntries(timespan).map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.badRequest().build());
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

}
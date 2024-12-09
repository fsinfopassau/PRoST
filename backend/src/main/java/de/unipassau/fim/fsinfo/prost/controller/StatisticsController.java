package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.CompositeMetricType;
import de.unipassau.fim.fsinfo.prost.data.ItemMetricType;
import de.unipassau.fim.fsinfo.prost.data.TimeSpan;
import de.unipassau.fim.fsinfo.prost.data.UserMetricType;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dto.CompositeMetricDTO;
import de.unipassau.fim.fsinfo.prost.service.statistics.AbstractMetricCollector.MetricEntry;
import de.unipassau.fim.fsinfo.prost.service.statistics.MetricService;
import de.unipassau.fim.fsinfo.prost.service.statistics.composite.HourlyActivityMetricCollector;
import de.unipassau.fim.fsinfo.prost.service.statistics.composite.ItemPurchaseMetricCollector;
import de.unipassau.fim.fsinfo.prost.service.statistics.item.AbstractItemMetricCollector;
import de.unipassau.fim.fsinfo.prost.service.statistics.user.AbstractUserMetricCollector;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

  private final ItemPurchaseMetricCollector itemPurchaseMetricCollector;
  private final HourlyActivityMetricCollector hourlyActivityMetricCollector;
  private final MetricService metricService;

  @Autowired
  public StatisticsController(ItemPurchaseMetricCollector metricCollector,
      HourlyActivityMetricCollector hourlyActivityMetricCollector,
      MetricService metricService) {
    this.hourlyActivityMetricCollector = hourlyActivityMetricCollector;
    this.itemPurchaseMetricCollector = metricCollector;
    this.metricService = metricService;
  }

  @GetMapping("/metric/item")
  public ResponseEntity<List<MetricEntry<ShopItem>>> getItemMetric(
      ItemMetricType type, TimeSpan timespan) {
    return AbstractItemMetricCollector.getMetricEntries(type, timespan).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @GetMapping("/metric/user")
  public ResponseEntity<List<MetricEntry<ProstUser>>> getUserMetric(
      UserMetricType type, TimeSpan timespan) {
    return AbstractUserMetricCollector.getMetricEntries(type, timespan).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @GetMapping("/metric/composite")
  public ResponseEntity<List<CompositeMetricDTO>> getCompositeMetric(
      CompositeMetricType type, TimeSpan timespan) {
    if (type == CompositeMetricType.ITEM_USER) {
      return itemPurchaseMetricCollector.getCompositeMetricEntries(timespan).map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.badRequest().build());
    } else if (type == CompositeMetricType.HOURLY_ACTIVITY) {
      return hourlyActivityMetricCollector.getCompositeMetricEntries(timespan)
          .map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.badRequest().build());
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/reset")
  public ResponseEntity<String> reset() {
    return ResponseEntity.ok(metricService.resetMetric() + "");
  }

}
package de.unipassau.fim.fsinfo.prost.service.statistics;

import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;

public abstract class AbstractItemLeaderboard extends AbstractLeaderboard<ShopItem> {

  public AbstractItemLeaderboard() {
    super(ShopItem.class);
  }
}

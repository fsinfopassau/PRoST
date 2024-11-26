package de.unipassau.fim.fsinfo.prost.service.statistics;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractUserLeaderboard extends AbstractLeaderboard<ProstUser> {

  public AbstractUserLeaderboard() {
    super(ProstUser.class);
  }

  @Override
  protected List<LeaderboardEntry<ProstUser>> mapToLeaderboardEntries(
      ConcurrentHashMap<String, BigDecimal> leaderboardEntries) {
    List<LeaderboardEntry<ProstUser>> result = super.mapToLeaderboardEntries(leaderboardEntries);

    List<LeaderboardEntry<ProstUser>> sanitized = new ArrayList<>();

    for (LeaderboardEntry<ProstUser> entry : result) {
      if (entry.entity().getHidden()) {
        sanitized.add(getSanitizedEntry(entry));
      } else {
        sanitized.add(entry);
      }
    }

    return sanitized;
  }

  protected LeaderboardEntry<ProstUser> getSanitizedEntry(LeaderboardEntry<ProstUser> entry) {
    return new LeaderboardEntry<>("", ProstUser.getAnonymous(), entry.value());
  }
}

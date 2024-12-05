import { UserLeaderboardType, TimeSpan, CompositeLeaderboardType } from "../../Types/Statistics";
import { CompositeLeaderboard, UserLeaderboard } from "./Leaderboard";

export function AllUserStatistics(props: { timeSpan: TimeSpan }) {
  const { timeSpan } = props;

  return (
    <div className="GridContainer" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(25rem, 1fr))" }}>
      <UserLeaderboard
        type={UserLeaderboardType.MVP}
        timeSpan={timeSpan}
        title="Stammkunden"
        desc="Gesamtausgaben"
        isMoney={true}
      />
      <UserLeaderboard
        type={UserLeaderboardType.DEBT_CUSTOMER}
        timeSpan={timeSpan}
        title="Schuldensammler"
        desc="Guthaben"
        isMoney={true}
      />
      <UserLeaderboard
        type={UserLeaderboardType.LOYAL_CUSTOMER}
        timeSpan={timeSpan}
        title="Loyalisten"
        desc="Käufe"
        isMoney={false}
      />
      <UserLeaderboard
        type={UserLeaderboardType.KIOSK_CUSTOMER}
        timeSpan={timeSpan}
        title="Kiosk Loyalisten"
        desc="Käufe"
        isMoney={false}
      />
      <UserLeaderboard
        type={UserLeaderboardType.LUXURY_CUSTOMER}
        timeSpan={timeSpan}
        title="Oberschicht"
        desc="∅ Einkaufs-Preis"
        isMoney={true}
      />
      <CompositeLeaderboard
        type={CompositeLeaderboardType.ITEM_USER}
        timeSpan={timeSpan}
        title="Suchtis"
        desc="Käufe"
        isMoney={false}
      />
    </div>
  );
}

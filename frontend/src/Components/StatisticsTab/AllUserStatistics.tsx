import { UserLeaderboardType, TimeSpan } from "../../Types/Statistics";
import { UserLeaderboard } from "./Leaderboard";

export function AllUserStatistics(props: { timeSpan: TimeSpan }) {
  const { timeSpan } = props;

  return (
    <div className="GridContainer" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(25rem, 1fr))" }}>
      <UserLeaderboard
        type={UserLeaderboardType.MVP}
        timeSpan={timeSpan}
        title="MVP"
        desc="Gesamtausgaben"
        isMoney={true}
      />
      <UserLeaderboard
        type={UserLeaderboardType.DEBT_CUSTOMER}
        timeSpan={timeSpan}
        title="Debtcollectors"
        desc="Guthaben"
        isMoney={true}
      />
      <UserLeaderboard
        type={UserLeaderboardType.LOYAL_CUSTOMER}
        timeSpan={timeSpan}
        title="Loyalists"
        desc="Käufe"
        isMoney={false}
      />
      <UserLeaderboard
        type={UserLeaderboardType.KIOSK_CUSTOMER}
        timeSpan={timeSpan}
        title="Kiosk Loyalists"
        desc="Käufe"
        isMoney={false}
      />
      <UserLeaderboard
        type={UserLeaderboardType.LUXURY_CUSTOMER}
        timeSpan={timeSpan}
        title="Luxorious"
        desc="Avg. Preis"
        isMoney={true}
      />
    </div>
  );
}

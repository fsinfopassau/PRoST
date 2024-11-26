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
        desc="Alltime Total"
        isMoney={true}
      />
      <UserLeaderboard
        type={UserLeaderboardType.DEBT_CUSTOMER}
        timeSpan={timeSpan}
        title="Debtcollectors"
        desc="Balance"
        isMoney={true}
      />
      <UserLeaderboard
        type={UserLeaderboardType.LOYAL_CUSTOMER}
        timeSpan={timeSpan}
        title="Loyalists"
        desc="Transactions"
        isMoney={true}
      />
    </div>
  );
}

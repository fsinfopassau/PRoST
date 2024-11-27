import { ItemLeaderboard } from "./Leaderboard";
import { ItemLeaderboardType, TimeSpan } from "../../Types/Statistics";

export function AllItemStatistics(props: { timeSpan: TimeSpan }) {
  const { timeSpan } = props;

  return (
    <div className="GridContainer" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(25rem, 1fr))" }}>
      <ItemLeaderboard
        type={ItemLeaderboardType.TOP_SELLING_ITEMS}
        timeSpan={timeSpan}
        title="Topseller"
        desc=""
        isMoney={false}
      />
      <ItemLeaderboard
        type={ItemLeaderboardType.ITEM_REVENUE}
        timeSpan={timeSpan}
        title="Item-Revenue"
        desc="Revenue"
        isMoney={true}
      />
    </div>
  );
}

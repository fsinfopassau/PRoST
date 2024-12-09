import { ItemLeaderboard } from "./Leaderboard";
import { ItemMetricType, TimeSpan } from "../../Types/Statistics";

export function AllItemStatistics(props: { timeSpan: TimeSpan }) {
  const { timeSpan } = props;

  return (
    <div className="GridContainer" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(25rem, 1fr))" }}>
      <ItemLeaderboard
        type={ItemMetricType.TOP_SELLING_ITEMS}
        timeSpan={timeSpan}
        title="Verkaufsschlager"
        desc="Käufe"
        isMoney={false}
      />
      <ItemLeaderboard
        type={ItemMetricType.ITEM_REVENUE}
        timeSpan={timeSpan}
        title="Lukratives"
        desc="Einnahmen"
        isMoney={true}
      />
    </div>
  );
}

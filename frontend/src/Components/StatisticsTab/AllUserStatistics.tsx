import { UserMetricType, TimeSpan, CompositeMetricType } from "../../Types/Statistics";
import { CompositeLeaderboard, UserLeaderboard } from "./Leaderboard";

export function AllUserStatistics(props: { timeSpan: TimeSpan }) {
  const { timeSpan } = props;

  return (
    <div className="GridContainer" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(25rem, 1fr))" }}>
      <UserLeaderboard
        type={UserMetricType.MVP}
        timeSpan={timeSpan}
        title="Stammkunden"
        desc="Gesamtausgaben"
        isMoney={true}
      />
      <UserLeaderboard
        type={UserMetricType.DEBT_CUSTOMER}
        timeSpan={timeSpan}
        title="Schuldensammler"
        desc="Guthaben"
        isMoney={true}
      />
      <UserLeaderboard
        type={UserMetricType.LOYAL_CUSTOMER}
        timeSpan={timeSpan}
        title="Loyalisten"
        desc="Käufe"
        isMoney={false}
      />
      <UserLeaderboard
        type={UserMetricType.KIOSK_CUSTOMER}
        timeSpan={timeSpan}
        title="Kiosk Loyalisten"
        desc="Käufe"
        isMoney={false}
      />
      <UserLeaderboard
        type={UserMetricType.LUXURY_CUSTOMER}
        timeSpan={timeSpan}
        title="Oberschicht"
        desc="∅ Einkaufs-Preis"
        isMoney={true}
      />
      <CompositeLeaderboard
        type={CompositeMetricType.ITEM_USER}
        timeSpan={timeSpan}
        title="Suchtis"
        desc="Käufe"
        isMoney={false}
      />
    </div>
  );
}

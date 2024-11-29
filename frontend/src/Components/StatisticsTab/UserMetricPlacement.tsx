import { useEffect, useState } from "react";
import { LeaderboardUserEntry, TimeSpan, UserLeaderboardType } from "../../Types/Statistics";
import { getUserLeaderboard } from "../../Queries";
import { User } from "../../Types/User";
import { formatMoney } from "../../Format";

export function UserMetricPlacement(props: {
  user: User;
  type: UserLeaderboardType;
  timeSpan: TimeSpan;
  title: string;
  desc: string;
  isMoney: boolean;
}) {
  const { user, type, timeSpan, title, desc, isMoney } = props;
  const [stats, setStats] = useState<LeaderboardUserEntry>();
  const [position, setPosition] = useState<number>();

  useEffect(() => {
    getUserLeaderboard(type, timeSpan).then((l) => {
      if (l) {
        l.map((a, index) => {
          if (a.entity.id == user.id) {
            setStats(a);
            setPosition(index);
          }
        });
      }
    });
  }, [timeSpan]);

  return (
    <div className="DisplayCard">
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "0rem", flexDirection: "column" }}>
          <div className="bold">{title}</div>
          <h2>{position ? position + 1 : ""}</h2>
          <div>
            {desc}: {isMoney ? formatMoney(stats?.value) : stats?.value}
          </div>
        </div>
      </div>
    </div>
  );
}

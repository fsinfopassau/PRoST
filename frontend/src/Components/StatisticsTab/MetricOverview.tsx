import { useEffect, useState } from "react";
import { UserMetricEntry, TimeSpan, UserMetricType } from "../../Types/Statistics";
import { getUserMetric } from "../../Queries";
import { User } from "../../Types/User";
import { formatMoney } from "../../Format";

export function UserMetricPlacement(props: {
  user: User;
  type: UserMetricType;
  timeSpan: TimeSpan;
  title: string;
  desc: string;
  isMoney: boolean;
}) {
  const { user, type, timeSpan, title, desc, isMoney } = props;
  const [stats, setStats] = useState<UserMetricEntry>();
  const [position, setPosition] = useState<number>();

  useEffect(() => {
    getUserMetric(type, timeSpan).then((l) => {
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
          <h2>{position != undefined ? position + 1 : ""}</h2>
          <div>
            {desc}: {isMoney ? formatMoney(stats?.value) : stats?.value}
          </div>
        </div>
      </div>
    </div>
  );
}
export function MetricInfo(props: { value: string; title: string; desc: string }) {
  const { value, title, desc } = props;

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
          <h2>{value}</h2>
          <div>{desc}</div>
        </div>
      </div>
    </div>
  );
}

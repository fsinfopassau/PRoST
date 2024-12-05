import { useLocation, useNavigate, useParams } from "react-router-dom";
import { UserSummaryCard } from "./UserSummaryCard";
import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { getUser } from "../../Queries";
import { ErrorComponent } from "../Util/ErrorTab";
import { Tabs, TabsList, TabsTrigger } from "@radix-ui/react-tabs";
import { CompositeLeaderboardType, TimeSpan, toTimeSpan, UserLeaderboardType } from "../../Types/Statistics";
import { UserMetricPlacement } from "./MetricOverview";
import { CompositeMetricPieChart } from "../Chart/PieChart";

export function UserStatistics() {
  const [user, setUser] = useState<User>();
  const { userId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const [timeSpan, setTimeSpan] = useState<TimeSpan>(TimeSpan.ALL_TIME);

  useEffect(() => {
    if (userId) {
      getUser(userId).then((user) => {
        setUser(user);
      });
    }

    const urlTimeSpan = getTimeSpanFromPath();
    if (timeSpan !== urlTimeSpan) {
      setTimeSpan(urlTimeSpan);
    }
  }, [location.search]);

  function handleTimeSpanChange(newTimeSpan: TimeSpan) {
    const queryParams = new URLSearchParams(location.search);
    queryParams.set("timeSpan", newTimeSpan);
    navigate(`${location.pathname}?${queryParams.toString()}`, { replace: true });
    setTimeSpan(newTimeSpan);
  }

  function getTimeSpanFromPath() {
    const queryParams = new URLSearchParams(location.search);
    const param = queryParams.get("timeSpan");
    const timeSpan = param ? toTimeSpan(param) : TimeSpan.ALL_TIME;

    if (timeSpan) {
      return timeSpan;
    }
    return TimeSpan.ALL_TIME;
  }

  if (user) {
    return (
      <>
        <div style={{ display: "flex", justifyContent: "center" }}>
          <div className="GridContainer" style={{ flexGrow: "0" }}>
            <UserSummaryCard user={user} />
          </div>
        </div>
        <div style={{ display: "flex", justifyContent: "center" }}>
          <Tabs value={timeSpan}>
            <TabsList className="TabsList">
              <TabsTrigger
                value={TimeSpan.ALL_TIME}
                className="TabsTrigger"
                onClick={() => handleTimeSpanChange(TimeSpan.ALL_TIME)}
              >
                All
              </TabsTrigger>
              <TabsTrigger
                value={TimeSpan.MONTH}
                className="TabsTrigger"
                onClick={() => handleTimeSpanChange(TimeSpan.MONTH)}
              >
                30d
              </TabsTrigger>
              <TabsTrigger
                value={TimeSpan.WEEK}
                className="TabsTrigger"
                onClick={() => handleTimeSpanChange(TimeSpan.WEEK)}
              >
                7d
              </TabsTrigger>
            </TabsList>
          </Tabs>
        </div>
        <div className="" style={{ display: "flex", flexFlow: "row wrap", justifyContent: "space-around" }}>
          <UserMetricPlacement
            user={user}
            type={UserLeaderboardType.LOYAL_CUSTOMER}
            title="Loyalisten"
            desc="Käufe"
            isMoney={false}
            timeSpan={timeSpan}
          />
          <UserMetricPlacement
            user={user}
            type={UserLeaderboardType.KIOSK_CUSTOMER}
            title="Kiosk Loyalisten"
            desc="Käufe"
            isMoney={false}
            timeSpan={timeSpan}
          />
          <UserMetricPlacement
            user={user}
            type={UserLeaderboardType.LUXURY_CUSTOMER}
            title="Oberschicht"
            desc="∅ Preis"
            isMoney={true}
            timeSpan={timeSpan}
          />
          <CompositeMetricPieChart
            type={CompositeLeaderboardType.ITEM_USER}
            title="Favoriten"
            desc=""
            time={timeSpan}
            filterFirst={false}
            dataKey={user.id}
          />
        </div>
      </>
    );
  } else {
    return <ErrorComponent />;
  }
}

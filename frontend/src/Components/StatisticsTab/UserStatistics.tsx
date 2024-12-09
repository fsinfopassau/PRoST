import { useLocation, useNavigate, useParams } from "react-router-dom";
import { UserSummaryCard } from "./UserSummaryCard";
import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { getOwnUser, getUser } from "../../Queries";
import { ErrorComponent } from "../Util/ErrorTab";
import { Tabs, TabsList, TabsTrigger } from "@radix-ui/react-tabs";
import { CompositeMetricType, TimeSpan, toTimeSpan, UserMetricType } from "../../Types/Statistics";
import { UserMetricPlacement } from "./MetricOverview";
import { CompositeMetricPieChart } from "../Chart/PieChart";
import { getAuthorizedUser } from "../../SessionInfo";
import { CompositeMetricLineChart } from "../Chart/LineChart";

export function UserStatistics() {
  const [user, setUser] = useState<User>();
  const { userId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const [timeSpan, setTimeSpan] = useState<TimeSpan>(TimeSpan.ALL_TIME);

  useEffect(() => {
    if (userId) {
      const authU = getAuthorizedUser();
      if (userId == authU?.id) {
        getOwnUser().then((u) => {
          setUser(u);
        });
      } else {
        getUser(userId).then((user) => {
          setUser(user);
        });
      }
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

  if (user?.hidden) {
    return (
      <>
        <div style={{ display: "flex", justifyContent: "center" }}>
          <div className="GridContainer" style={{ flexGrow: "0" }}>
            <UserSummaryCard user={user} />
          </div>
        </div>
        <h3>Keine Statistiken für private Profile</h3>
      </>
    );
  } else if (user) {
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
            type={UserMetricType.LOYAL_CUSTOMER}
            title="Loyalisten"
            desc="Käufe"
            isMoney={false}
            timeSpan={timeSpan}
          />
          <UserMetricPlacement
            user={user}
            type={UserMetricType.KIOSK_CUSTOMER}
            title="Kiosk Loyalisten"
            desc="Käufe"
            isMoney={false}
            timeSpan={timeSpan}
          />
          <UserMetricPlacement
            user={user}
            type={UserMetricType.LUXURY_CUSTOMER}
            title="Oberschicht"
            desc="∅ Preis"
            isMoney={true}
            timeSpan={timeSpan}
          />
        </div>
        <div className="" style={{ display: "flex", flexFlow: "row wrap", justifyContent: "space-around" }}>
          <CompositeMetricPieChart
            type={CompositeMetricType.ITEM_USER}
            title="Favoriten"
            desc=""
            time={timeSpan}
            filterFirst={false}
            dataKey={user.id}
          />
          <CompositeMetricLineChart
            type={CompositeMetricType.HOURLY_ACTIVITY}
            title="Aktivität"
            desc="Käufe zur Tageszeit"
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

import { useLocation, useNavigate } from "react-router-dom";
import { BarChartIcon, CookieIcon, PersonIcon, ReloadIcon } from "@radix-ui/react-icons";
import { AllSystemStatistics } from "./AllSystemStatistics";
import { AllUserStatistics } from "./AllUserStatistics";
import { AllItemStatistics } from "./AllItemStatistics";
import { Tabs, TabsList, TabsTrigger } from "@radix-ui/react-tabs";
import { TimeSpan, toTimeSpan } from "../../Types/Statistics";
import { useEffect, useState } from "react";
import { resetMetrics } from "../../Queries";
import { isAdmin } from "../../SessionInfo";

export function Statistics() {
  const location = useLocation();
  const navigate = useNavigate();
  const [timeSpan, setTimeSpan] = useState<TimeSpan>(TimeSpan.ALL_TIME);

  useEffect(() => {
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

  function tabUpdate(newValue: string) {
    const currentSearch = location.search; // Gets the query string (?key=value)
    if (newValue === "system") {
      navigate(`/stats${currentSearch}`);
    } else {
      navigate(`/stats/${newValue}${currentSearch}`);
    }
  }

  function head() {
    return (
      <>
        <div>
          <h2 style={{ display: "flex", gap: "1rem" }}>
            <Tabs value={getSelectedTabValue()}>
              <TabsList className="TabsList">
                <TabsTrigger value="system" className="TabsTrigger" onClick={() => tabUpdate("system")}>
                  <BarChartIcon />
                </TabsTrigger>
                <TabsTrigger value="users" className="TabsTrigger" onClick={() => tabUpdate("users")}>
                  <PersonIcon />
                </TabsTrigger>
                <TabsTrigger value="items" className="TabsTrigger" onClick={() => tabUpdate("items")}>
                  <CookieIcon />
                </TabsTrigger>
              </TabsList>
            </Tabs>

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
            {isAdmin() ? (
              <div className="Button" onClick={resetMetrics}>
                <ReloadIcon />
              </div>
            ) : (
              <></>
            )}
          </h2>
        </div>
      </>
    );
  }

  function getSelectedTabValue() {
    const splits = location.pathname.split("/");

    if (splits.length == 3) {
      return splits[2];
    }
    return "system";
  }

  function getTimeSpanFromPath() {
    const queryParams = new URLSearchParams(location.search);
    const param = queryParams.get("timeSpan");
    const timeSpan = param ? toTimeSpan(param) : TimeSpan.WEEK;

    if (timeSpan) {
      return timeSpan;
    }
    return TimeSpan.ALL_TIME;
  }

  function getStatistics() {
    const split = getSelectedTabValue();

    switch (split) {
      case "system":
        return <AllSystemStatistics timeSpan={timeSpan} />;
      case "users":
        return <AllUserStatistics timeSpan={timeSpan} />;
      case "items":
        return <AllItemStatistics timeSpan={timeSpan} />;
    }
    return <AllSystemStatistics timeSpan={timeSpan} />;
  }

  return (
    <>
      {head()} {getStatistics()}
    </>
  );
}

import { ScrollArea, ScrollAreaScrollbar, ScrollAreaThumb, ScrollAreaViewport } from "@radix-ui/react-scroll-area";
import { HistoryEntryDisplay } from "./HistoryEntryDisplay";
import { useEffect, useState } from "react";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { User } from "../../Types/User";
import { ShopItem } from "../../Types/ShopItem";
import { getAllShopItems, getAllUsers, getHistory, getItemLeaderboard } from "../../Queries";
import { formatMoney } from "../../Format";
import { MetricInfo } from "./MetricOverview";
import { isOnlyUser } from "../../SessionInfo";
import { ItemLeaderboardType, TimeSpan } from "../../Types/Statistics";
import { ItemMetricPieChart } from "../Chart/PieChart";

export function AllSystemStatistics(props: { timeSpan: TimeSpan }) {
  const [history, setHistory] = useState<ShopHistoryEntry[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [items, setItems] = useState<ShopItem[]>([]);
  const [totalRevenue, setTotalRevenue] = useState<number>(0);
  const { timeSpan } = props;

  useEffect(reloadShopItems, []);

  function reloadShopItems() {
    getAllShopItems().then((itemList) => {
      if (itemList) setItems(itemList);
    });
  }

  useEffect(() => {
    getAllUsers()
      .then((userList) => {
        if (userList === undefined) {
          setUsers([]);
        } else {
          setUsers(userList);
        }
      })
      .catch(() => {
        setUsers([]);
      });
  }, []);

  useEffect(() => {
    getItemLeaderboard(ItemLeaderboardType.ITEM_REVENUE, timeSpan).then((l) => {
      if (l) {
        let v = 0;
        l.forEach((e) => {
          v += e.value;
        });
        setTotalRevenue(v);
      }
    });
  }, [timeSpan]);

  useEffect(() => {
    getHistory(10, 0).then((historyList) => {
      if (historyList) setHistory(historyList.content);
    });
  }, []);

  function getUserDebt(): number {
    let num = 0;
    for (const user of users) {
      num += user.balance;
    }
    return num;
  }

  return (
    <>
      {isOnlyUser() ? (
        <></>
      ) : (
        <ScrollArea className="DisplayCard">
          <ScrollAreaViewport style={{ maxHeight: "20rem" }}>
            <h3 className="bold">Kürzliche Käufe</h3>
            <table className="Table">
              <tbody>
                {history.map((item) => (
                  <HistoryEntryDisplay entry={item} key={item.id} showHidden={false} />
                ))}
              </tbody>
            </table>
          </ScrollAreaViewport>
          <ScrollAreaScrollbar className="Scrollbar" orientation="vertical">
            <ScrollAreaThumb className="ScrollbarThumb" />
          </ScrollAreaScrollbar>
          <ScrollAreaScrollbar className="Scrollbar" orientation="horizontal">
            <ScrollAreaThumb className="ScrollbarThumb" />
          </ScrollAreaScrollbar>
        </ScrollArea>
      )}
      <div className="GridContainer" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(24rem, 1fr))" }}>
        <MetricInfo title="Nutzer" value={String(users.length)} desc="" />
        <MetricInfo title="Gegenstände" value={String(items.length)} desc="" />
        <MetricInfo title="Guthaben" value={formatMoney(getUserDebt())} desc="" />
        <MetricInfo title="Einnahmen" value={formatMoney(totalRevenue)} desc="" />
      </div>
      <div className="" style={{ display: "flex", flexFlow: "row wrap", justifyContent: "space-around" }}>
        <ItemMetricPieChart
          title="Verkaufsschlager"
          desc=""
          type={ItemLeaderboardType.TOP_SELLING_ITEMS}
          time={timeSpan}
        />
      </div>
    </>
  );
}

import { ScrollArea, ScrollAreaScrollbar, ScrollAreaThumb, ScrollAreaViewport } from "@radix-ui/react-scroll-area";
import { Separator } from "@radix-ui/react-separator";
import { HistoryEntryDisplay } from "./HistoryEntryDisplay";
import { useEffect, useState } from "react";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { User } from "../../Types/User";
import { ShopItem } from "../../Types/ShopItem";
import { getAllShopItems, getAllUsers, getHistory } from "../../Queries";
import { formatMoney } from "../../Format";

export function AllSystemStatistics() {
  const [history, setHistory] = useState<ShopHistoryEntry[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [items, setItems] = useState<ShopItem[]>([]);

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
    <div className="GridContainer">
      <div className="DisplayCard">
        <h3 className="bold">Status</h3>
        <Separator className="Separator" />
        <table className="Table">
          <tbody>
            <tr className="table-entry">
              <th className="name">Nutzer</th>
              <th className="">:</th>
              <th className="name">{users.length}</th>
            </tr>
            <tr className="table-entry">
              <th className="amount">Nutzer-Budget</th>
              <th className="">:</th>
              <th className="balance">{formatMoney(getUserDebt())}</th>
            </tr>
            <tr className="table-entry">
              <th className="name">Gegenstände</th>
              <th className="">:</th>
              <th className="name">{items.length}</th>
            </tr>
          </tbody>
        </table>
      </div>
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
    </div>
  );
}

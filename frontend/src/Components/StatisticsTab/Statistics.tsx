import { useEffect, useState } from "react";
import { getHistory } from "../Util/Queries";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { HistoryEntryDisplay } from "./HistoryEntryDisplay";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";
import { Link } from "react-router-dom";

export function Statistics() {
  const [history, setHistory] = useState<ShopHistoryEntry[]>([]);

  useEffect(() => {
    getHistory(20).then((historyList) => {
      if (historyList) setHistory(historyList.reverse());
    });
  }, []);

  return (
    <>
      <div className="CardContainer">
        <ScrollArea className="DisplayCard">
          <ScrollAreaViewport style={{ maxHeight: "20rem" }}>
            <div className="bold">Kürzlich:</div>
            <table>
              <tbody>
                {history.map((item) => (
                  <HistoryEntryDisplay entry={item} key={item.id} />
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
        <div className="DisplayCard">
          <Link to={`/stats/users`} className="bold">
            Nutzer
          </Link>
        </div>
        <div className="DisplayCard">More STATS..</div>
        <div className="DisplayCard">More STATS..</div>
        <div className="DisplayCard">More STATS..</div>
      </div>
    </>
  );
}

import { useEffect, useState } from "react";
import { getHistory } from "../../Queries";
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
    getHistory(20, 0).then((historyList) => {
      if (historyList) setHistory(historyList.content);
    });
  }, []);

  return (
    <>
      <div className="CardContainer">
        <div className="DisplayCard">
          <h3 className="bold">Sprungbrett</h3>
          <div className="SelectionContainer">
            <Link to={`/stats/users`} className="bold Button">
              Nutzer
            </Link>
            <Link to={`/stats/users/admin`} className="bold Button">
              Admin TEMP
            </Link>
          </div>
        </div>
        <ScrollArea className="DisplayCard">
          <ScrollAreaViewport style={{ maxHeight: "20rem" }}>
            <h3 className="bold">Kürzlich</h3>
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
        <div className="DisplayCard">More STATS..</div>
        <div className="DisplayCard">More STATS..</div>
        <div className="DisplayCard">More STATS..</div>
      </div>
    </>
  );
}

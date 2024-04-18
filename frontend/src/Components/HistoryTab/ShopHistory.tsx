import { useEffect, useState } from "react";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { getHistory } from "../../Queries";
import { HistoryEntryDisplay } from "../StatisticsTab/HistoryEntryDisplay";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";

export function ShopHistory() {
  const [history, setHistory] = useState<ShopHistoryEntry[]>([]);

  useEffect(() => {
    getHistory(20).then((historyList) => {
      if (historyList) setHistory(historyList);
    });
  }, []);

  return (
    <>
      <ScrollArea className="DisplayCard" style={{ maxWidth: "60rem" }}>
        <ScrollAreaViewport>
          <h2>Historie</h2>
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
    </>
  );
}

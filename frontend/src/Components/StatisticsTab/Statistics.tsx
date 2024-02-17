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

export function Statistics() {
  const [history, setHistory] = useState<ShopHistoryEntry[]>([]);

  useEffect(() => {
    getHistory(5).then((historyList) => {
      if (historyList) setHistory(historyList.reverse());
    });
  }, []);

  return (
    <>
      <ScrollArea className="history-container DisplayCard">
        <ScrollAreaViewport>
          <div className="bold">Kürzlich:</div>
          {history.map((item) => (
            <HistoryEntryDisplay entry={item} />
          ))}
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

import { useEffect, useState } from "react";
import { getHistory } from "../Util/Queries";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { HistoryEntryDisplay } from "./HistoryEntryDisplay";
import {
  ScrollArea,
  ScrollAreaCorner,
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
      <ScrollArea className="history-container">
        <ScrollAreaViewport className="ScrollAreaViewport">
          <div style={{ padding: "15px 20px" }}>
            <div className="Text">Kürzlich:</div>
            {history.map((item) => (
              <HistoryEntryDisplay entry={item} />
            ))}
          </div>
        </ScrollAreaViewport>
        <ScrollAreaScrollbar
          className="ScrollAreaScrollbar"
          orientation="vertical"
        >
          <ScrollAreaThumb className="ScrollAreaThumb" />
        </ScrollAreaScrollbar>
        <ScrollAreaScrollbar
          className="ScrollAreaScrollbar"
          orientation="horizontal"
        >
          <ScrollAreaThumb className="ScrollAreaThumb" />
        </ScrollAreaScrollbar>
        <ScrollAreaCorner className="ScrollAreaCorner" />
      </ScrollArea>
      <br />
    </>
  );
}

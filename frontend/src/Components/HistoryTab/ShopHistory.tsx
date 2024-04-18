import { useEffect, useState } from "react";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { getHistory, getOwnHistory } from "../../Queries";
import { HistoryEntryDisplay } from "../StatisticsTab/HistoryEntryDisplay";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";

export function ShopHistory(props: { personal: boolean }) {
  const [history, setHistory] = useState<ShopHistoryEntry[]>([]);

  useEffect(() => {
    if (props.personal) {
      getOwnHistory(20).then((historyList) => {
        if (historyList) setHistory(historyList);
      });
    } else {
      getHistory(20).then((historyList) => {
        if (historyList) setHistory(historyList);
      });
    }
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

import { useEffect, useState } from "react";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { getHistory, getOwnHistory, getUserHistory } from "../../Queries";
import { HistoryEntryDisplay } from "../StatisticsTab/HistoryEntryDisplay";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";
import { Separator } from "@radix-ui/react-separator";

export function ShopHistory(props: { personal: boolean }) {
  const [selectedPage, setSelectedPage] = useState(0);
  const [searchValue, setSearchValue] = useState("");
  const [totalPages, setTotalPages] = useState(0);
  const [minPage, setMinPages] = useState(0);
  const [maxPage, setMaxPages] = useState(3);
  const [history, setHistory] = useState<ShopHistoryEntry[]>([]);

  useEffect(() => {
    reloadTransactions();
  }, [selectedPage, searchValue]);

  useEffect(() => {
    if (selectedPage === 0) selectPage(0);
  });

  function selectPage(n: number) {
    setSelectedPage(n);

    const range = 5;

    // Calculate the range to show 5 page indicators, adjusting for edge cases
    let minPage = Math.max(0, n - range);
    let maxPage = Math.min(totalPages - 1, n + range);

    // Adjust the range if the selected page is near the beginning or end
    if (n <= range) {
      minPage = 0;
      maxPage = Math.min(range * 2, totalPages - 1);
    } else if (n >= totalPages - 1 - range) {
      minPage = Math.max(totalPages - 2 - range * 2, 0);
      maxPage = totalPages - 1;
    }

    // Update the state with the adjusted range
    setMinPages(minPage);
    setMaxPages(maxPage);
  }

  function handleKeyDown(event: React.KeyboardEvent) {
    if (event.key === "Enter") {
      reloadTransactions();
    }
  }
  function reloadTransactions() {
    if (props.personal) {
      getOwnHistory(10, selectedPage).then((historyPage) => {
        if (historyPage) {
          setHistory(historyPage.content);
          setTotalPages(historyPage.totalPages + 1);
        }
      });
    } else {
      if (searchValue.trim().length != 0) {
        getUserHistory(searchValue, 10, selectedPage).then((historyPage) => {
          if (historyPage) {
            setHistory(historyPage.content);
            setTotalPages(historyPage.totalPages + 1);
          }
        });
      } else {
        getHistory(10, selectedPage).then((historyPage) => {
          if (historyPage) {
            setHistory(historyPage.content);
            setTotalPages(historyPage.totalPages + 1);
          }
        });
      }
    }
  }

  return (
    <>
      <ScrollArea className="DisplayCard" style={{ maxWidth: "60rem" }}>
        <ScrollAreaViewport>
          <h2>Historie</h2>

          {props.personal ? (
            <></>
          ) : (
            <>
              <div id="tableSearch">
                <input
                  className="Input"
                  type="text"
                  onChange={(e) => setSearchValue(e.target.value)}
                  onKeyDown={handleKeyDown}
                  placeholder="Käufer Id"
                />
              </div>
              <Separator className="Separator" />
            </>
          )}

          <table className="Table">
            <tbody>
              {history.map((item) => (
                <HistoryEntryDisplay entry={item} key={item.id} />
              ))}
            </tbody>
          </table>

          <Separator className="Separator" />

          <div className="PageBar">
            {Array.from({ length: totalPages - 1 }, (_, index) => {
              if (index >= minPage && index <= maxPage) {
                return (
                  <div
                    key={"p" + index}
                    className={`PageButton ${
                      selectedPage === index ? "Selected" : ""
                    }`}
                    onClick={() => selectPage(index)}
                  >
                    {index + 1}
                  </div>
                );
              }
            }).filter(Boolean)}
          </div>
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

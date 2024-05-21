import { useEffect, useState } from "react";
import { getPersonalInvoices } from "../../Queries";
import { Invoice } from "../../Types/Invoice";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";
import { InvoiceSelectDisplay } from "../InvoiceTab/InvoiceSelectDisplay";
import { Separator } from "@radix-ui/react-separator";

export function PersonalInvoiceView() {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [selectedPage, setSelectedPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [minPage, setMinPages] = useState(0);
  const [maxPage, setMaxPages] = useState(3);

  useEffect(reloadInvoices, []);

  function reloadInvoices() {
    getPersonalInvoices().then((result) => {
      if (result && result.content) {
        setInvoices(result.content);
        setTotalPages(result.totalPages + 1);
      }
    });
  }

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

  return (
    <div className="SingleCardContainer">
      <ScrollArea
        className="DisplayCard"
        style={{ height: "100%", maxWidth: "60rem" }}
      >
        <ScrollAreaViewport>
          <h2>Rechnungen</h2>

          <table className="Table">
            <tbody>
              {invoices.map((invoice, index) => (
                <InvoiceSelectDisplay
                  key={index}
                  invoice={invoice}
                  onSelect={() => { }}
                  selected={undefined}
                />
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
                    className={`PageButton ${selectedPage === index ? "Selected" : ""
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
    </div>
  );
}

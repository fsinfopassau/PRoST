import { useEffect, useState } from "react";
import { Invoice } from "../../Types/Invoice";
import {
  deleteInvoices,
  getAllInvoices,
  mailInvoices,
  publishInvoices,
} from "../../Queries";
import { InvoiceSelectDisplay } from "./InvoiceSelectDisplay";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";
import { Separator } from "@radix-ui/react-separator";
import {
  CheckIcon,
  EnvelopeClosedIcon,
  EyeOpenIcon,
  FileMinusIcon,
  FilePlusIcon,
  PaperPlaneIcon,
} from "@radix-ui/react-icons";
import ConfirmInvoices from "./ConfirmInvoices";
import InvoiceCreation from "./InvoiceCreation";
import { toast } from "react-toastify";

export function InvoiceTab() {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [selectedItems, setSelectedItems] = useState<number[]>([]);
  const [selectedPage, setSelectedPage] = useState(0);
  const [searchValue, setSearchValue] = useState("");
  const [totalPages, setTotalPages] = useState(0);
  const [minPage, setMinPages] = useState(0);
  const [maxPage, setMaxPages] = useState(3);
  const [mailed, setMailed] = useState<boolean | undefined>(undefined);

  useEffect(() => {
    reloadInvoices();
  }, [selectedPage, searchValue, mailed]);

  useEffect(() => {
    if (selectedPage === 0)
      selectPage(0);
    console.log(minPage, maxPage);
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

    console.log("select", minPage, maxPage);
  }

  function handleKeyDown(event: React.KeyboardEvent) {
    if (event.key === "Enter") {
      reloadInvoices();
    }
  }

  function reloadInvoices() {
    getAllInvoices(selectedPage, searchValue, mailed).then((result) => {
      if (result && result.content) {
        setInvoices(result.content);
        setTotalPages(result.totalPages + 1);
      }
    });
  }

  function cycleMailed() {
    switch (mailed) {
      case undefined:
        setMailed(true);
        break;
      case true:
        setMailed(false);
        break;
      case false:
        setMailed(undefined);
        break;
    }
  }

  function handleSelect(id: number) {
    setSelectedItems((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  }

  function toggleAll() {
    if (isAllSelected()) {
      setSelectedItems([]);
      return;
    }

    setSelectedItems([]);
    invoices.forEach((i) => {
      if (!i.mailed) handleSelect(i.id);
    });
  }

  function publishSelected() {
    if (selectedItems.length === 0) return;

    publishInvoices(selectedItems)
      .then((result) => {
        if (result) {
          toast.success(result.length + " veröffentlicht!");
          setSelectedItems([]);
          reloadInvoices();
        } else {
          toast.error("Veröffentlichungsfehler!");
        }
      })
      .catch(() => {
        toast.error("Verbindungsfehler!");
      });
  }

  function mailSelected() {
    if (selectedItems.length === 0) return;

    mailInvoices(selectedItems)
      .then((result) => {
        if (result) {
          toast.success(result.length + " versendet!");
          setSelectedItems([]);
          reloadInvoices();
        } else {
          toast.error("Mailfehler!");
        }
      })
      .catch(() => {
        toast.error("Verbindungsfehler!");
      });
  }

  function deleteSelected() {
    if (selectedItems.length === 0) return;

    deleteInvoices(selectedItems)
      .then((result) => {
        if (result) {
          toast.warn(result.length + " gelöscht!");
          setSelectedItems([]);
          reloadInvoices();
        } else {
          toast.error("Löschfehler!");
        }
      })
      .catch(() => {
        toast.error("Verbindungsfehler!");
      });
  }

  function isAllSelected(): boolean {
    if (
      invoices === undefined ||
      invoices.length === 0 ||
      selectedItems.length === 0
    ) {
      return false;
    }
    return invoices.every((invoice) => {
      if (selectedItems.includes(invoice.id)) {
        return true;
      } else if (invoice.mailed) {
        return true;
      }
      return false;
    });
  }

  function getSelectedInvoices(): Invoice[] {
    return invoices.filter((invoice) => selectedItems.includes(invoice.id));
  }

  return (
    <>
      <ScrollArea
        className="DisplayCard"
        style={{ height: "100%", maxWidth: "60rem" }}
      >
        <ScrollAreaViewport>
          <h2>Rechnungen</h2>

          <div id="invoiceSearch">
            <input
              className="Input"
              type="text"
              onChange={(e) => setSearchValue(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Nutzer Id" />
            <div onClick={cycleMailed} className={mailed === undefined ? "Button" : (mailed ? "Button green" : "Button red")}><EnvelopeClosedIcon /></div>
          </div>

          <Separator className="Separator" />

          <div className="SpreadContainer" style={{ padding: "0 .5rem 0.5rem 0.5rem" }}>
            <div style={{ display: "flex", gap: "1rem", alignItems: "center" }}>
              {isAllSelected() ? (
                <div className="Toggle green" onClick={toggleAll}>
                  <CheckIcon />
                </div>
              ) : (
                <div className="Toggle" onClick={toggleAll}></div>
              )}

              {selectedItems.length !== 0 ? (
                <div className="SpreadContainer">
                  <ConfirmInvoices
                    dialogTitle="Rechnungen Löschen?"
                    invoices={getSelectedInvoices()}
                    onSubmit={deleteSelected}
                  >
                    <div className="Button red">
                      <FileMinusIcon />
                    </div>
                  </ConfirmInvoices>

                  <ConfirmInvoices
                    dialogTitle="Rechnungen Veröffentlichen?"
                    invoices={getSelectedInvoices()}
                    onSubmit={publishSelected}
                  >
                    <div className="Button orange">
                      <EyeOpenIcon />
                    </div>
                  </ConfirmInvoices>

                  <ConfirmInvoices
                    dialogTitle="Rechnungen Verschicken?"
                    invoices={getSelectedInvoices()}
                    onSubmit={mailSelected}
                  >
                    <div className="Button orange">
                      <PaperPlaneIcon />
                    </div>
                  </ConfirmInvoices>
                </div>
              ) : (
                <></>
              )}
            </div>

            <div style={{ display: "flex", gap: "1rem", alignItems: "center" }}>
              <InvoiceCreation onSubmit={reloadInvoices}>
                <div className="Button green">
                  <FilePlusIcon />
                </div>
              </InvoiceCreation>
            </div>
          </div>

          <table className="InvoiceTable">
            <tbody>
              {invoices.map((invoice, index) => (
                <InvoiceSelectDisplay
                  key={index}
                  invoice={invoice}
                  onSelect={handleSelect}
                  selected={selectedItems.includes(invoice.id)}
                />
              ))}
            </tbody>
          </table>

          <Separator className="Separator" />

          <div className="PageBar">
            {Array.from({ length: totalPages - 1 }, (_, index) => {
              if (index >= minPage && index <= maxPage) {
                return <div
                  key={"p" + index}
                  className={`PageButton ${selectedPage === index ? 'Selected' : ''}`}
                  onClick={() => selectPage(index)}
                >
                  {index + 1}
                </div>
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

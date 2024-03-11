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
  EyeOpenIcon,
  FileMinusIcon,
  FilePlusIcon,
  PaperPlaneIcon,
} from "@radix-ui/react-icons";
import ConfirmInvoices from "./ConfirmInvoices";

export function InvoiceTab() {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [selectedItems, setSelectedItems] = useState<number[]>([]);

  useEffect(reloadInvoices, []);

  function reloadInvoices() {
    getAllInvoices().then((result) => {
      if (result && result.content) {
        setInvoices(result.content);
      }
    });
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
    publishInvoices(selectedItems).then((result) => {
      if (result) {
        reloadInvoices();
        setSelectedItems([]);
      }
    });
  }

  function mailSelected() {
    mailInvoices(selectedItems).then((result) => {
      if (result) {
        reloadInvoices();
        setSelectedItems([]);
      }
    });
  }

  function deleteSelected() {
    deleteInvoices(selectedItems).then((result) => {
      if (result) {
        reloadInvoices();
        setSelectedItems([]);
      }
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
      <ScrollArea className="DisplayCard" style={{ height: "100%" }}>
        <ScrollAreaViewport>
          <h2>Rechnungen</h2>

          <div className="SpreadContainer" style={{ padding: "0 .5rem" }}>
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
              <div className="Button green">
                <FilePlusIcon />
              </div>
            </div>
          </div>

          <Separator className="Separator" />
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

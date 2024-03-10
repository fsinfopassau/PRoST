import { useEffect, useState } from "react";
import { Invoice } from "../../Types/Invoice";
import { getAllInvoices, getAllUsers } from "../../Queries";
import { InvoiceEntryDisplay } from "./InvoiceEntryDisplay";

export function InvoiceTab() {
  const [invoices, setInvoices] = useState<Invoice[]>([]);

  useEffect(() => {
    getAllInvoices().then((result) => {
      if (result && result.content) {
        setInvoices(result.content);
      }
    });
  }, []);
  return (
    <>
      <div className="DisplayCard">
        <h2>Rechnungen</h2>
        <table className="InvoiceTable">
          <tbody>
            {invoices.map((invoice, index) => (
              <InvoiceEntryDisplay key={index} invoice={invoice} />
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}

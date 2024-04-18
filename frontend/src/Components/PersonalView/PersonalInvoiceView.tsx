import { useEffect, useState } from "react";
import { getPersonalInvoices } from "../../Queries";
import { Invoice } from "../../Types/Invoice";
import { convertTimestampToTime, formatMoney } from "../../Format";
import ScrollDialog from "../Util/ScrollDialog";
import { InfoCircledIcon } from "@radix-ui/react-icons";
import { InvoiceDetails } from "../InvoiceTab/InvoiceDetails";

export function PersonalInvoiceView() {
  const [invoices, setInvoices] = useState<Invoice[]>([]);

  useEffect(reloadInvoices, []);

  function reloadInvoices() {
    getPersonalInvoices().then((result) => {
      if (result && result.content) {
        setInvoices(result.content);
      }
    });
  }
  function totalAmounts(invoice: Invoice): number {
    let count = 0;

    invoice.amounts.forEach((entry) => {
      count += entry.amount;
    });

    return count;
  }

  return (
    <div className="CardContainer">
      <div className="DisplayCard">
        <h3>Rechnungen</h3>
        <table className="Table">
          <tbody>
            {invoices?.map((invoice, index) => (
              <tr key={index}>
                <th className="icon">
                  <ScrollDialog
                    title="Rechnung"
                    trigger={
                      <div className="green">
                        <InfoCircledIcon />
                      </div>
                    }
                    onSubmit={() => {}}
                  >
                    <InvoiceDetails invoice={invoice} />
                  </ScrollDialog>
                </th>
                <th className="amount">{totalAmounts(invoice)}</th>
                <th className="balance bold">{formatMoney(invoice.balance)}</th>
                <th className="date">
                  {convertTimestampToTime(invoice.timestamp)}
                </th>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

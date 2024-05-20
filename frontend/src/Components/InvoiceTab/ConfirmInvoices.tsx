import { PropsWithChildren } from "react";
import { Invoice } from "../../Types/Invoice";
import { convertTimestampToTime, formatMoney } from "../../Format";
import { Link } from "react-router-dom";
import ScrollDialog from "../Util/ScrollDialog";
import { InfoCircledIcon } from "@radix-ui/react-icons";
import { InvoiceDetails } from "./InvoiceDetails";

interface CustomComponentProps {
  dialogTitle: string;
  invoices: Invoice[];
  onSubmit: () => void;
}

const ConfirmInvoices: React.FC<PropsWithChildren<CustomComponentProps>> = ({
  children,
  dialogTitle,
  invoices,
  onSubmit,
}) => {
  function totalAmounts(invoice: Invoice): number {
    let count = 0;

    invoice.amounts.forEach((entry) => {
      count += entry.amount;
    });

    return count;
  }

  return (
    <ScrollDialog
      title={dialogTitle}
      trigger={<div>{children} </div>}
      onSubmit={onSubmit}
    >
      <table className="Table">
        <tbody>
          {invoices.map((invoice, index) => (
            <tr
              key={index}
              className={totalAmounts(invoice) === 0 ? "table-entry important-color" : "table-entry"}
            >
              <th className="icon left">
                <ScrollDialog
                  title="Rechnung"
                  trigger={
                    <div className="good-color">
                      <InfoCircledIcon />
                    </div>
                  }
                  onSubmit={() => { }}
                >
                  <InvoiceDetails invoice={invoice} />
                </ScrollDialog>
              </th>
              <th className="name bold">
                <Link to={`/stats/users/${invoice.userId}`} className="bold">
                  {invoice.userDisplayName}
                </Link>
              </th>
              <th className="amount">{totalAmounts(invoice)}</th>
              <th className="balance right bold">{formatMoney(invoice.balance)}</th>
              <th className="date right">
                {convertTimestampToTime(invoice.timestamp)}
              </th>
            </tr>
          ))}
        </tbody>
      </table>
    </ScrollDialog>
  );
};

export default ConfirmInvoices;

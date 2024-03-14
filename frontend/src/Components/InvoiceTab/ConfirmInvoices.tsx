import { PropsWithChildren } from "react";
import { Invoice } from "../../Types/Invoice";
import { convertTimestampToTime, formatMoney } from "../../Format";
import { Link } from "react-router-dom";
import ScrollDialog from "../Util/ScrollDialog";

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
      <table className="InvoiceTable">
        <tbody>
          {invoices.map((invoice, index) => (
            <tr key={index}>
              <th className="name bold">
                <Link to={`/stats/users/${invoice.userId}`} className="bold">
                  {invoice.userDisplayName}
                </Link>
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
    </ScrollDialog>
  );
};

export default ConfirmInvoices;
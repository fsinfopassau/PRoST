import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
  DialogTrigger,
} from "@radix-ui/react-dialog";
import { CheckIcon, Cross2Icon } from "@radix-ui/react-icons";
import { PropsWithChildren, useState } from "react";
import { Invoice } from "../../Types/Invoice";
import { convertTimestampToTime, formatMoney } from "../../Format";
import { Link } from "react-router-dom";

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
  const [open, setOpen] = useState(false);

  function handleKeyDown(event: React.KeyboardEvent) {
    if (event.key === "Enter") {
      onSubmit();
      setOpen(false);
    }
    if (event.key === "Escape") {
      setOpen(false);
    }
  }

  function totalAmounts(invoice: Invoice): number {
    let count = 0;

    invoice.amounts.forEach((entry) => {
      count += entry.amount;
    });

    return count;
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <div>{children}</div>
      </DialogTrigger>
      <DialogPortal>
        <DialogOverlay className="DialogOverlay" />
        <DialogContent className="DialogContent" onKeyDown={handleKeyDown}>
          <div>
            <DialogClose asChild className="DialogClose">
              <button className="IconButton" aria-label="Close">
                <Cross2Icon />
              </button>
            </DialogClose>
          </div>
          <DialogTitle className="DialogTitle">{dialogTitle}</DialogTitle>
          <div className="DialogDescription">
            <table className="InvoiceTable">
              <tbody>
                {invoices.map((invoice, index) => (
                  <tr key={index}>
                    <th className="name bold">
                      <Link
                        to={`/stats/users/${invoice.userId}`}
                        className="bold"
                      >
                        {invoice.userDisplayName}
                      </Link>
                    </th>
                    <th className="amount">{totalAmounts(invoice)}</th>
                    <th className="balance bold">
                      {formatMoney(invoice.balance)}
                    </th>
                    <th className="date">
                      {convertTimestampToTime(invoice.timestamp)}
                    </th>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div
            style={{
              display: "flex",
              marginTop: 25,
              justifyContent: "flex-end",
            }}
          >
            <DialogClose asChild onClick={onSubmit}>
              <button className="Button">
                <CheckIcon />
              </button>
            </DialogClose>
          </div>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
};

export default ConfirmInvoices;

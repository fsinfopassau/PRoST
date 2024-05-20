import {
  CheckIcon,
  EnvelopeClosedIcon,
  EyeNoneIcon,
  EyeOpenIcon,
  InfoCircledIcon,
  LockClosedIcon,
} from "@radix-ui/react-icons";
import { convertTimestampToTime, formatMoney } from "../../Format";
import { Invoice } from "../../Types/Invoice";
import { Link } from "react-router-dom";
import ScrollDialog from "../Util/ScrollDialog";
import { InvoiceDetails } from "./InvoiceDetails";

export function InvoiceSelectDisplay(props: {
  invoice: Invoice;
  onSelect: (id: number) => void;
  selected: boolean | undefined;
}) {
  const { invoice, onSelect, selected } = props;

  function totalAmounts(): number {
    let count = 0;

    invoice.amounts.forEach((entry) => {
      count += entry.amount;
    });

    return count;
  }

  return (
    <tr className={totalAmounts() === 0 ? "table-entry orange" : "table-entry"}>
      {selected === undefined ? <></> :
        <th className="icon">
          {invoice.mailed ? (
            <div>
              <LockClosedIcon />
            </div>
          ) : selected ? (
            <div
              className="CheckBox green"
              onClick={() => {
                onSelect(invoice.id);
              }}
            >
              <CheckIcon />
            </div>
          ) : (
            <div
              className="CheckBox"
              onClick={() => {
                onSelect(invoice.id);
              }}
            ></div>
          )}
        </th>
      }
      {invoice.mailed ? (
        <th className="icon">
          <div className="green">
            <EnvelopeClosedIcon />
          </div>
        </th>
      ) : invoice.published ? (
        <th className="icon">
          <div className="orange">
            <EyeOpenIcon />
          </div>
        </th>
      ) : (
        <th className="icon">
          <div className="red">
            <EyeNoneIcon />
          </div>
        </th>
      )}
      <th className="icon">
        <ScrollDialog
          title="Rechnung"
          trigger={
            <div className="green">
              <InfoCircledIcon />
            </div>
          }
          onSubmit={() => { }}
        >
          <InvoiceDetails invoice={invoice} />
        </ScrollDialog>
      </th>
      <th className="name bold left">
        <Link to={`/stats/users/${invoice.userId}`} className="bold">
          {invoice.userDisplayName}
        </Link>
      </th>
      <th className="amount">{totalAmounts()}</th>
      <th className="balance bold right">{formatMoney(invoice.balance)}</th>
      <th className="date right">{convertTimestampToTime(invoice.timestamp)}</th>
    </tr>
  );
}

import {
  CheckIcon,
  EnvelopeClosedIcon,
  EyeNoneIcon,
  LockClosedIcon,
} from "@radix-ui/react-icons";
import { convertTimestampToTime, formatMoney } from "../../Format";
import { Invoice } from "../../Types/Invoice";
import { useEffect, useState } from "react";
import { getUserDisplayName } from "../../Types/User";
import { Link } from "react-router-dom";

export function InvoiceEntryDisplay(props: {
  invoice: Invoice;
  onSelect: (id: number) => void;
  selected: boolean;
}) {
  const { invoice, onSelect, selected } = props;
  const [userName, setUserName] = useState<string>("");

  useEffect(() => {
    getUserDisplayName(invoice.userId).then((name) => {
      setUserName(name);
    });
  }, []);

  function totalAmounts(): number {
    let count = 0;

    invoice.amounts.forEach((entry) => {
      count += entry.amount;
    });

    return count;
  }

  return (
    <tr>
      <th className="icon">
        {invoice.mailed ? (
          <div>
            <LockClosedIcon />
          </div>
        ) : selected ? (
          <div
            className="Toggle green"
            onClick={() => {
              onSelect(invoice.id);
            }}
          >
            <CheckIcon />
          </div>
        ) : (
          <div
            className="Toggle"
            onClick={() => {
              onSelect(invoice.id);
            }}
          ></div>
        )}
      </th>
      {invoice.mailed ? (
        <th className="icon">
          <div className="green">
            <EnvelopeClosedIcon />
          </div>
        </th>
      ) : invoice.public ? (
        <th className="icon">
          <div className="red">
            <EnvelopeClosedIcon />
          </div>
        </th>
      ) : (
        <th className="icon">
          <div className="red">
            <EyeNoneIcon />
          </div>
        </th>
      )}
      <th className="name bold">
        <Link to={`/stats/users/${invoice.userId}`} className="bold">
          {userName}
        </Link>
      </th>
      <th className="amount">{totalAmounts()}</th>
      <th className="balance bold">{formatMoney(invoice.balance)}</th>
      <th className="date">{convertTimestampToTime(invoice.timestamp)}</th>
    </tr>
  );
}

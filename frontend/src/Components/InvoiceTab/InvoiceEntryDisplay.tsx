import { EnvelopeClosedIcon, EyeNoneIcon } from "@radix-ui/react-icons";
import { convertTimestampToTime, formatMoney } from "../../Format";
import { Invoice } from "../../Types/Invoice";
import { useEffect, useState } from "react";
import { getUserDisplayName } from "../../Types/User";
import { Link } from "react-router-dom";

export function InvoiceEntryDisplay(props: { invoice: Invoice }) {
  const { invoice } = props;
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
      {invoice.mailed ? (
        <th className="mail green">
          <EnvelopeClosedIcon />
        </th>
      ) : (
        <th className="mail red">
          <EyeNoneIcon />
        </th>
      )}
      <th className="bold name">
        <Link to={`/stats/users/${invoice.userId}`} className="bold">
          {userName}
        </Link>
      </th>
      <th className="amount">{totalAmounts()}</th>
      <th className="bold balance">{formatMoney(invoice.balance)}</th>
      <th className="date">{convertTimestampToTime(invoice.timestamp)}</th>
    </tr>
  );
}

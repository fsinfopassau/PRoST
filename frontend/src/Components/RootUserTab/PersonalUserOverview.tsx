import { Link } from "react-router-dom";
import { Invoice } from "../../Types/Invoice";
import { User } from "../../Types/User";
import { UserSummaryCard } from "../StatisticsTab/UserSummaryCard";
import { convertTimestampToTime, formatMoney } from "../../Format";
import { useEffect, useState } from "react";
import { getPersonalInvoices } from "../../Queries";

export function PersonalUserOverview(props: { user: User }) {
  const { user } = props;
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
    <>
      <UserSummaryCard user={user} isSelf={true} />
      <div className="CardContainer">
        <div className="DisplayCard">
          <h3>Statistiken</h3>
          <p>...</p>
        </div>
        <div className="DisplayCard">
          <h3>Rechnungen</h3>
          <table className="InvoiceTable">
            <tbody>
              {invoices?.map((invoice, index) => (
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
        <div className="DisplayCard">
          <h3>Ranking</h3>
          <p>...</p>
        </div>
        <div className="DisplayCard">
          <h3>Achievements</h3>
          <p>...</p>
        </div>
      </div>
    </>
  );
}

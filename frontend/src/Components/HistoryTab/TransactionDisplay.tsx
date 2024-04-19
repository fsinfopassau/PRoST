import { DoubleArrowRightIcon } from "@radix-ui/react-icons";
import { Transaction } from "../../Types/Transaction";
import { Link } from "react-router-dom";
import { convertTimestampToTime, formatMoney } from "../../Format";

export function TransactionDisplay(props: { transaction: Transaction }) {
  const { transaction } = props;

  return (
    <>
      <tr>
        <th className="left">
          {convertTimestampToTime(transaction.timestamp)}
        </th>
        <th className="name bold">{transaction.transactionType}</th>
        <th className="name bold">
          <div style={{ display: "flex" }}>
            (
            <Link to={`/stats/users/${transaction.bearerId}`} className="bold">
              {transaction.bearerId}
            </Link>
            )
            <Link to={`/stats/users/${transaction.senderId}`} className="bold">
              {transaction.senderId}
            </Link>
            <div>
              <DoubleArrowRightIcon />
            </div>
            <Link
              to={`/stats/users/${transaction.receiverId}`}
              className="bold"
            >
              {transaction.receiverId}
            </Link>
          </div>
        </th>
        <th className="balance bold">{formatMoney(transaction.amount)}</th>
      </tr>
    </>
  );
}

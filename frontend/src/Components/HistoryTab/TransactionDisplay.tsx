import { DoubleArrowRightIcon } from "@radix-ui/react-icons";
import { Transaction } from "../../Types/Transaction";
import { Link } from "react-router-dom";
import { convertTimestampToTime, formatMoney } from "../../Format";

export function TransactionDisplay(props: { transaction: Transaction }) {
  const { transaction } = props;

  return (
    <tr className="table-entry">
      <th className="left date">{convertTimestampToTime(transaction.timestamp)}</th>
      <th className="bold">{transaction.transactionType}</th>
      <th className="">
        <div className="horizontalContainer">
          (
          <Link to={`/stats/users/${transaction.bearerId}`} className="bold">
            {transaction.bearerId}
          </Link>
          )
          <Link to={`/stats/users/${transaction.senderId}`} className="bold">
            {transaction.senderId}
          </Link>
          <DoubleArrowRightIcon />
          <Link to={`/stats/users/${transaction.receiverId}`} className="bold">
            {transaction.receiverId}
          </Link>
        </div>
      </th>
      <th className="balance bold right">
        {transaction.transactionType.toString() === "CHANGE" ? (
          <div className="horizontalContainer bold right">
            {formatMoney(transaction.previous)}
            <DoubleArrowRightIcon />
            {formatMoney(transaction.amount)}
          </div>
        ) : (
          <>{formatMoney(transaction.amount)}</>
        )}
      </th>
    </tr>
  );
}

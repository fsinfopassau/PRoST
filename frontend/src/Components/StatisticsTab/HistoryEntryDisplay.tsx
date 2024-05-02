import { Link } from "react-router-dom";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { formatMoney, getTimeSince } from "../../Format";

export function HistoryEntryDisplay(props: { entry: ShopHistoryEntry }) {
  const { entry } = props;

  function getAmount(): string {
    if (entry.amount == 1) return "";
    return entry.amount + " x";
  }

  return (
    <tr className="table-entry">
      <th className="name left">
        <Link to={`/stats/users/${entry.userId}`} className="bold">
          {entry.userDisplayName}
        </Link>
      </th>
      <th className="balance right">{formatMoney(-entry.itemPrice * entry.amount)}</th>
      <th className="amount right">{getAmount()}</th>
      <th className="left">{entry.itemDisplayName}</th>
      <th className="right">{getTimeSince(entry.timestamp)}</th>
    </tr>
  );
}

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
    <tr className="table-entry" style={{ display: "flex" }}>
      <th>
        <Link to={`/stats/users/${entry.userId}`} className="bold">
          {entry.userDisplayName}
        </Link>
      </th>
      <th className="last">{formatMoney(-entry.itemPrice * entry.amount)}</th>
      <th className="last">{getAmount()}</th>
      <th className="">{entry.itemDisplayName}</th>
      <th className="last">{getTimeSince(entry.timestamp)}</th>
    </tr>
  );
}

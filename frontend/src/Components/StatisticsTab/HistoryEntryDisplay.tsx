import { Link } from "react-router-dom";
import { ShopHistoryEntry, getTimeSince } from "../../Types/ShopHistory";
import { formatMoney } from "../../Types/User";

export function HistoryEntryDisplay(props: { entry: ShopHistoryEntry }) {
  const { entry } = props;

  return (
    <tr className="history-entry">
      <th className="bold">
        <Link to={`/stats/users/${entry.userId}`} className="bold">
          {entry.userId}
        </Link>
      </th>
      <th className="last">-{formatMoney(entry.price)}</th>
      <th className="">{entry.itemId}</th>
      <th className="last">{getTimeSince(entry.timestamp)}</th>
    </tr>
  );
}

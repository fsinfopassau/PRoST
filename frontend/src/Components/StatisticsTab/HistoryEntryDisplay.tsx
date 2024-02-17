import { ShopHistoryEntry, getTimeSince } from "../../Types/ShopHistory";
import { formatMoney } from "../../Types/User";

export function HistoryEntryDisplay(props: { entry: ShopHistoryEntry }) {
  const { entry } = props;

  return (
    <table className="history-entry">
      <th className="bold">{entry.userName}</th>
      <th className="last">-{formatMoney(entry.price)}</th>
      <th className="">{entry.itemId}</th>
      <th className="last">{getTimeSince(entry.timestamp)}</th>
    </table>
  );
}

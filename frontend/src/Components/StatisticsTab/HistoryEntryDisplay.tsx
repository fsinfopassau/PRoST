import { ShopHistoryEntry, getTimeSince } from "../../Types/ShopHistory";
import { formatBalance } from "../../Types/User";

export function HistoryEntryDisplay(props: { entry: ShopHistoryEntry }) {
  const { entry } = props;

  return (
    <table className="history-entry">
      <th className="bold">{entry.userName}</th>
      <th className="bold">{entry.itemId}</th>
      <th>-{formatBalance(entry.price)}</th>
      <th>{getTimeSince(entry.timestamp)}</th>
    </table>
  );
}

import { Link } from "react-router-dom";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { formatMoney, getTimeSince } from "../../Format";
import { EyeClosedIcon } from "@radix-ui/react-icons";

export function HistoryEntryDisplay(props: { entry: ShopHistoryEntry }) {
  const { entry } = props;

  function getAmount(): string {
    if (entry.amount == 1) return "";
    return entry.amount + " x";
  }

  console.log(entry);

  if(entry.hidden){
    return (
      <tr className="table-entry">
        <th className="name left">
          Anonyme 🍍
        </th>
        <th className="balance right"><EyeClosedIcon/></th>
        <th className="amount right"></th>
        <th className="left"><EyeClosedIcon/></th>
        <th className="right time">{getTimeSince(entry.timestamp)}</th>
      </tr>
    );
  } else {
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
        <th className="right time">{getTimeSince(entry.timestamp)}</th>
      </tr>
    );
  }
}

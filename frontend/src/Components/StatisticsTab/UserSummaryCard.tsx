import { Link } from "react-router-dom";
import { User, formatMoney } from "../../Types/User";
import { Separator } from "@radix-ui/react-separator";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { useEffect, useState } from "react";
import { getUserHistory } from "../Util/Queries";

export function UserSummaryCard(props: { user: User }) {
  const { user } = props;
  const [history, setHistory] = useState<ShopHistoryEntry[]>([]);

  useEffect(() => {
    getUserHistory(user, 3).then((historyList) => {
      if (historyList) setHistory(historyList.reverse());
    });
  }, []);

  return (
    <div className="UserSummary">
      <div className="DisplayCard">
        <div className="TopHContainer">
          <Link to={`/stats/users/${user.username}`} className="bold">
            {user.username}
          </Link>
          <div>👑 🍺</div>
        </div>
        <Separator className="Separator" />
        <div className="BottomHContainer">
          <div>
            <div className="bold">Letzte:</div>
            {history.map((entry) => (
              <div>{entry.itemId}</div>
            ))}
          </div>
          <Separator className="Separator" decorative orientation="vertical" />
          <div className="Balance">{formatMoney(user.balance)}</div>
        </div>
      </div>
    </div>
  );
}

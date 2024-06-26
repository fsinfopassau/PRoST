import { Link } from "react-router-dom";
import { getLevel, User } from "../../Types/User";
import { Separator } from "@radix-ui/react-separator";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { useEffect, useState } from "react";
import { getOwnHistory, getUserHistory } from "../../Queries";
import { formatMoney } from "../../Format";
import { getAuthorizedUser } from "../../SessionInfo";
import { LevelProgressDisplay } from "./UserLevelDisplay";

interface Props {
  user: User;
}

export function UserSummaryCard(props: Props) {
  const { user } = props;
  const [history, setHistory] = useState<ShopHistoryEntry[]>([]);

  useEffect(() => {
    if (user.id === getAuthorizedUser()?.id) {
      getOwnHistory(3, 0).then((page) => {
        if (page) setHistory(page.content);
      });
    } else {
      getUserHistory(user.id, 3, 0).then((historyList) => {
        if (historyList) setHistory(historyList.content);
      });
    }
  }, []);

  function getDisplayName(): string {
    return user.displayName ? user.displayName : user.id;
  }

  return (
    <div className="UserSummary">
      <div className="DisplayCard">
        <div className="SpreadContainer">
          <Link to={`/stats/users/${user.id}`} className="bold">
            {getDisplayName()}
          </Link>
          <div>💙🦆💙</div>
        </div>
        <div style={{ textAlign: "center" }} className="LevelNumberDisplay">
          {getLevel(user)}
        </div>
        <LevelProgressDisplay value={(user.totalSpent % 5) / 5} />
        <div className="SpreadContainer">
          <div>
            <div className="bold">Letzte:</div>
            {history.map((entry) => (
              <div key={entry.id}>{entry.itemDisplayName}</div>
            ))}
          </div>
          <Separator className="Separator" decorative orientation="vertical" />
          <div className="Balance">{formatMoney(user.balance)}</div>
        </div>
      </div>
    </div>
  );
}

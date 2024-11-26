import { Link } from "react-router-dom";
import {
  UserLeaderboardType,
  LeaderboardUserEntry,
  TimeSpan,
  ItemLeaderboardType,
  LeaderboardItemEntry,
  convertUsersBalance,
  convertUsersSpent,
} from "../../Types/Statistics";
import { useEffect, useState } from "react";
import { getAllUsers, getItemLeaderboard, getUserLeaderboard } from "../../Queries";
import { ScrollArea, ScrollAreaScrollbar, ScrollAreaThumb, ScrollAreaViewport } from "@radix-ui/react-scroll-area";
import { formatMoney } from "../../Format";

export function ItemLeaderboard(props: {
  type: ItemLeaderboardType;
  timeSpan: TimeSpan;
  title: string;
  desc: string;
  isMoney: boolean;
}) {
  const { type, timeSpan, title, desc, isMoney } = props;
  const [stats, setStats] = useState<LeaderboardItemEntry[]>([]);

  useEffect(() => {
    getItemLeaderboard(type, timeSpan)
      .then((l) => {
        if (l) {
          console.log(l);
          setStats(l);
        }
      })
      .catch(() => {
        setStats([]);
      });
  }, [timeSpan]);

  return (
    <ScrollArea className="DisplayCard">
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <h3>{title}</h3>
        <p>{desc}</p>
      </div>
      <ScrollAreaViewport style={{ maxHeight: "20rem" }}>
        <table className="Table">
          <tbody>
            {stats.map((entry, index) => {
              return (
                <LeaderboardEntry
                  entryDisplayName={entry.entity.displayName}
                  entryId={entry.entity.id}
                  entryValue={entry.value}
                  position={index + 1}
                  isUser={false}
                  key={index}
                  isMoney={isMoney}
                  anonymize={false}
                />
              );
            })}
          </tbody>
        </table>
      </ScrollAreaViewport>
      <ScrollAreaScrollbar className="Scrollbar" orientation="vertical">
        <ScrollAreaThumb className="ScrollbarThumb" />
      </ScrollAreaScrollbar>
      <ScrollAreaScrollbar className="Scrollbar" orientation="horizontal">
        <ScrollAreaThumb className="ScrollbarThumb" />
      </ScrollAreaScrollbar>
    </ScrollArea>
  );
}

export function UserLeaderboard(props: {
  type: UserLeaderboardType;
  timeSpan: TimeSpan;
  title: string;
  desc: string;
  isMoney: boolean;
}) {
  const { type, timeSpan, title, desc, isMoney } = props;
  const [stats, setStats] = useState<LeaderboardUserEntry[]>([]);

  useEffect(() => {
    if (type == UserLeaderboardType.DEBT_CUSTOMER) {
      getAllUsers(true).then((u) => {
        if (u) {
          setStats(convertUsersBalance(u).sort((a, b) => a.value - b.value));
        }
      });
    } else if (type == UserLeaderboardType.MVP) {
      getAllUsers(true).then((u) => {
        if (u) {
          setStats(convertUsersSpent(u).sort((a, b) => b.value - a.value));
        }
      });
    } else {
      getUserLeaderboard(type, timeSpan).then((l) => {
        if (l) {
          console.log(l);
          setStats(l);
        }
      });
    }
  }, [timeSpan]);

  return (
    <ScrollArea className="DisplayCard">
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <h3>{title}</h3>
        <p>{desc}</p>
      </div>
      <ScrollAreaViewport style={{ maxHeight: "20rem" }}>
        <table className="Table">
          <tbody>
            {stats.map((entry, index) => {
              return (
                <LeaderboardEntry
                  entryDisplayName={entry.entity.displayName}
                  entryId={entry.entity.id}
                  entryValue={entry.value}
                  position={index + 1}
                  isUser={true}
                  key={index}
                  isMoney={isMoney}
                  anonymize={entry.entity.hidden}
                />
              );
            })}
          </tbody>
        </table>
      </ScrollAreaViewport>
      <ScrollAreaScrollbar className="Scrollbar" orientation="vertical">
        <ScrollAreaThumb className="ScrollbarThumb" />
      </ScrollAreaScrollbar>
      <ScrollAreaScrollbar className="Scrollbar" orientation="horizontal">
        <ScrollAreaThumb className="ScrollbarThumb" />
      </ScrollAreaScrollbar>
    </ScrollArea>
  );
}

export function LeaderboardEntry(props: {
  entryDisplayName: string;
  entryId: string;
  entryValue: number;
  position: number;
  isUser: boolean;
  isMoney: boolean;
  anonymize: boolean;
}) {
  const { entryDisplayName, entryId, entryValue, position, isUser, isMoney, anonymize } = props;

  return (
    <tr className="table-entry">
      <th className="left">{position}</th>
      <th className="name left">
        {isUser && anonymize ? (
          <>Anonyme 🍍</>
        ) : isUser ? (
          <Link to={`/stats/users/${entryId}`} className="bold">
            {entryDisplayName}
          </Link>
        ) : (
          <>{entryDisplayName}</>
        )}
      </th>
      {isMoney ? <th className="right">{formatMoney(entryValue)}</th> : <th className="right">{entryValue}</th>}
    </tr>
  );
}

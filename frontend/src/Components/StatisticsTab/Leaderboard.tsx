import { Link } from "react-router-dom";
import {
  UserLeaderboardType,
  LeaderboardUserEntry,
  TimeSpan,
  ItemLeaderboardType,
  LeaderboardItemEntry,
} from "../../Types/Statistics";
import { useEffect, useState } from "react";
import { getItemLeaderboard, getUserLeaderboard } from "../../Queries";

export function ItemLeaderboard(props: { type: ItemLeaderboardType; timeSpan: TimeSpan }) {
  const { type, timeSpan } = props;
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
            />
          );
        })}
      </tbody>
    </table>
  );
}

export function UserLeaderboard(props: { type: UserLeaderboardType; timeSpan: TimeSpan }) {
  const { type, timeSpan } = props;
  const [stats, setStats] = useState<LeaderboardUserEntry[]>([]);

  useEffect(() => {
    getUserLeaderboard(type, timeSpan)
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
            />
          );
        })}
      </tbody>
    </table>
  );
}

export function LeaderboardEntry(props: {
  entryDisplayName: string;
  entryId: string;
  entryValue: number;
  position: number;
  isUser: boolean;
}) {
  const { entryDisplayName, entryId, entryValue, position, isUser } = props;

  return (
    <tr className="table-entry">
      <th className="left">{position}</th>
      <th className="name left">
        {isUser ? (
          <Link to={`/stats/users/${entryId}`} className="bold">
            {entryDisplayName}
          </Link>
        ) : (
          <>{entryDisplayName}</>
        )}
      </th>
      <th className="right">{entryValue}</th>
    </tr>
  );
}

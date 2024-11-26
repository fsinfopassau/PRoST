import { Link } from "react-router-dom";
import { UserLeaderboardType, LeaderboardUserEntry, TimeSpan } from "../../Types/Statistics";
import { useEffect, useState } from "react";
import { getUserLeaderboard } from "../../Queries";

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
              entryDisplayName={entry.item.displayName}
              entryId={entry.item.id}
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

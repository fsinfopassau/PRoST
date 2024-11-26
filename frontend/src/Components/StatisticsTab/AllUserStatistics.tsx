import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { getAllUsers } from "../../Queries";
import { UserSummaryCard } from "./UserSummaryCard";
import { UserLeaderboardType, TimeSpan } from "../../Types/Statistics";
import { UserLeaderboard } from "./Leaderboard";

export function AllUserStatistics(props: { timeSpan: TimeSpan }) {
  const { timeSpan } = props;
  const [users, setUsers] = useState<User[]>([]);

  console.log(timeSpan);

  useEffect(() => {
    getAllUsers()
      .then((userList) => {
        if (userList === undefined) {
          setUsers([]);
        } else {
          setUsers(userList);
        }
      })
      .catch(() => {
        setUsers([]);
      });
  }, []);

  useEffect(() => {
    console.log("change");
  }, [timeSpan]);

  return (
    <div className="GridContainer">
      <div className="DisplayCard">
        <UserLeaderboard type={UserLeaderboardType.LOYAL_CUSTOMER} timeSpan={timeSpan} />
      </div>
      <div className="DisplayCard">
        <div className="GridContainer">
          {Object.values(users).map((user, index) => (
            <UserSummaryCard key={index} user={user} />
          ))}
        </div>
      </div>
    </div>
  );
}

import { useEffect, useState } from "react";
import { User } from "../Types/User";
import { getAllUsers } from "../Queries";
import { UserSummaryCard } from "./StatisticsTab/UserSummaryCard";

export function Users() {
  const [users, setUsers] = useState<User[]>([]);

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

  return (
    <div className="GridContainer">
      {Object.values(users).map((user, index) => (
        <UserSummaryCard key={index} user={user} />
      ))}
    </div>
  );
}

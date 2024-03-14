import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { getAllUsers } from "../../Queries";
import { UserSummaryCard } from "./UserSummaryCard";

export function AllUsersStatistics() {
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
    <div className="CardContainer">
      {Object.values(users).map((user, index) => (
        <UserSummaryCard key={index} user={user} />
      ))}
    </div>
  );
}

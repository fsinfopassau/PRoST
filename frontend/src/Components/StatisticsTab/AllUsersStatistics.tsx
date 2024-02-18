import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { getAllUsers } from "../Util/Queries";
import { UserSummaryCard } from "./UserSummaryCard";

export function AllUsersStatistics() {
  const [users, setUsers] = useState<User[]>([]);

  useEffect(() => {
    getAllUsers().then((userList) => {
      setUsers(userList);
    });
  }, []);

  return (
    <div className="CardContainer">
      {users.map((user, index) => (
        <UserSummaryCard key={index} user={user} />
      ))}
    </div>
  );
}

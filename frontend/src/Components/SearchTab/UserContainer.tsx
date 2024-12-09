import { useEffect, useState } from "react";
import { sortUsersByDisplayName, User } from "../../Types/User";
import { UserBox } from "./UserSelectionDisplay";
import { getAllUsers, getUserMetric } from "../../Queries";
import Fuse from "fuse.js";
import { UserMetricEntry, TimeSpan, UserMetricType } from "../../Types/Statistics";

export function UserContainer() {
  const [searchValue, setSearchValue] = useState("");
  const [users, setUsers] = useState<User[] | undefined>([]);

  const [stats, setStats] = useState<UserMetricEntry[]>([]);

  useEffect(() => {
    getAllUsers()
      .then((userList) => {
        setUsers(userList);
      })
      .catch(() => {
        setUsers([]);
      });
    getUserMetric(UserMetricType.KIOSK_CUSTOMER, TimeSpan.WEEK).then((l) => {
      if (l) {
        setStats(
          l
            .sort((a, b) => b.value - a.value)
            .filter((e) => !e.entity.hidden && e.entity.kiosk && e.entity.enabled)
            .slice(0, 5)
        );
      }
    });
  }, []);

  function filter(users: User[]): User[] {
    sortUsersByDisplayName(users);

    if (searchValue.trim().length === 0) {
      return users.filter((user) => user.enabled && user.kiosk);
    }

    const fuse = new Fuse(users, {
      keys: ["id", "displayName"],
    });
    const results = fuse.search(searchValue).map((result) => result.item);

    return results.filter((user) => user.enabled);
  }

  function getRecent(): User[] {
    return stats.map((s) => {
      return s.entity;
    });
  }

  return (
    <>
      <input type="text" onChange={(e) => setSearchValue(e.target.value)} id="main-search" placeholder="Search" />
      <div className="DisplayCard">
        <h3>Loyale Nutzer</h3>
        <div style={{ display: "flex", gap: "1rem", justifyContent: "space-between" }}>
          {users === undefined ? <></> : getRecent().map((user, index) => <UserBox key={index} user={user} />)}
        </div>
      </div>
      <div className="SmallGridContainer">
        {users === undefined ? <></> : filter(users).map((user, index) => <UserBox key={index} user={user} />)}
      </div>
    </>
  );
}

import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { UserBox } from "./UserSelectionDisplay";
import { getAllUsers } from "../../Queries";
import Fuse from 'fuse.js';

export function UserContainer() {
  const [searchValue, setSearchValue] = useState("");
  const [users, setUsers] = useState<User[] | undefined>([]);

  useEffect(() => {
    getAllUsers()
      .then((userList) => {
        setUsers(userList);
      })
      .catch(() => {
        setUsers([]);
      });
  }, []);

  function filter(users: User[]): User[] {
    if (searchValue.trim().length === 0) {
      return users.filter(user => user.enabled === true);
    }

    const fuse = new Fuse(users, {
      keys: ['id', 'displayName']
    })
    const results = fuse.search(searchValue).map(result => result.item);

    return results.filter(user => user.enabled === true);
  }

  return (
    <>
      <input
        type="text"
        onChange={(e) => setSearchValue(e.target.value)}
        className="SearchInput"
        id="search"
        placeholder="Search"
      />
      <div className="SelectionContainer">
        {users === undefined ? (
          <></>
        ) : (
          filter(users).map((user, index) => (
            <UserBox key={index} user={user} />
          ))
        )}
      </div>
    </>
  );
}

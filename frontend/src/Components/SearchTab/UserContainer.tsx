import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { UserBox } from "./UserSelectionDisplay";
import { getAllUsers } from "../../Queries";

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

  function filter(users: User[], useFuzzy: boolean): User[] {
    const escapedSearch = searchValue.replace(/[.*+\-?^${}()|[\]\\]/g, "\\$&"); // Escape special regex characters

    const result = users.filter(
      (user) =>
        // Case-insensitive regex
        new RegExp(escapedSearch, "i").test(user.displayName) && user.enabled
    );

    if (useFuzzy) {
      // fuzzy
      const fuzzyRegex = escapedSearch.split("").join(".*");
      return users.filter(
        (user) =>
          new RegExp(fuzzyRegex, "i").test(user.displayName) && user.enabled
      );
    }

    return result;
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
          filter(users, true).map((user, index) => (
            <UserBox key={index} user={user} />
          ))
        )}
      </div>
    </>
  );
}

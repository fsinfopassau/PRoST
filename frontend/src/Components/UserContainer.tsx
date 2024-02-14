import { FC } from "react";
import { User } from "../DTO/User";
import { UserBox } from "./UserBox";

const UserContainer: FC<{ users: User[]; search: string }> = ({
  users,
  search,
}) => {
  function filter(users: User[], useFuzzy: boolean): User[] {
    if (search.trim() === "") return users;
    var escapedSearch = search.replace(/[.*+\-?^${}()|[\]\\]/g, "\\$&"); // Escape special regex characters

    const result = users.filter((user) =>
      // Case-insensitive regex
      new RegExp(escapedSearch, "i").test(user.name)
    );

    if (useFuzzy) {
      // fuzzy
      const fuzzyRegex = escapedSearch.split("").join(".*");
      const fuzzyResult = users.filter((user) =>
        new RegExp(fuzzyRegex, "i").test(user.name)
      );

      return fuzzyResult;
    }

    return result;
  }

  return (
    <>
      <div className="users-container">
        {filter(users, true).map((user, index) => (
          <UserBox key={index} {...user} />
        ))}
      </div>
    </>
  );
};

export default UserContainer;

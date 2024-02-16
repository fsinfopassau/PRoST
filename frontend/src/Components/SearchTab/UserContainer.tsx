import { User } from "../../Types/User";
import { UserBox } from "./UserSelectionDisplay";

export function UserContainer(props: { users: User[]; search: string }) {
  const { users, search } = props;

  function filter(users: User[], useFuzzy: boolean): User[] {
    if (search.trim() === "") return users;
    const escapedSearch = search.replace(/[.*+\-?^${}()|[\]\\]/g, "\\$&"); // Escape special regex characters

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
          <UserBox key={index} user={user} />
        ))}
      </div>
    </>
  );
}

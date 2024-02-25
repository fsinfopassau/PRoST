import { User } from "../../Types/User";
import { UserBox } from "./UserSelectionDisplay";

export function UserContainer(props: { users: User[]; search: string }) {
  const { users, search } = props;

  function filter(users: User[], useFuzzy: boolean): User[] {
    const escapedSearch = search.replace(/[.*+\-?^${}()|[\]\\]/g, "\\$&"); // Escape special regex characters

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
      <div className="SelectionContainer">
        {filter(users, true).map((user, index) => (
          <UserBox key={index} user={user} />
        ))}
      </div>
    </>
  );
}

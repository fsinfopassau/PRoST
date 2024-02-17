import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { getAllUsers } from "../Util/Queries";
import { UserContainer } from "./UserContainer";

export function UserSelection(props: { switchTheme: () => void }) {
  const [searchValue, setSearchValue] = useState("");
  const [users, setUsers] = useState<User[]>([]);

  useEffect(() => {
    getAllUsers().then((userList) => {
      setUsers(userList);
    });
  }, []);

  return (
    <>
      <h1>
        <img
          onClick={props.switchTheme}
          src="/icons/happy-manje/happy beer.svg"
        />
        KdV
      </h1>
      <input
        type="text"
        onChange={(e) => setSearchValue(e.target.value)}
        className="Input"
        id="search"
        placeholder="Search"
      />
      <div className="App">
        <UserContainer users={users} search={searchValue} />
      </div>
    </>
  );
}

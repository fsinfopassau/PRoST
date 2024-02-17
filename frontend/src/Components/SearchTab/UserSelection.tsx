import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { UserRole } from "../../Types/UserRole";
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

  const testUsers: User[] = [
    {
      username: "Rainer Zufall",
      balance: 51.4,
      enabled: true,
      role: UserRole.ADMINISTRATOR,
    },
    {
      username: "Erik Zion",
      balance: 3.2,
      enabled: true,
      role: UserRole.USER,
    },
    {
      username: "Kai Nepanik",
      balance: 25,
      enabled: true,
      role: UserRole.USER,
    },
    {
      username: "Frank Reich",
      balance: 2500,
      enabled: true,
      role: UserRole.USER,
    },
    {
      username: "Christian Harten",
      balance: -6.9,
      enabled: true,
      role: UserRole.USER,
    },
  ];

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

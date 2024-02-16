import { useState } from "react";
import { User } from "../../Types/User";
import { UserRole } from "../../Types/UserRole";
import { UserContainer } from "./UserContainer";

export function UserSelection(props: { switchTheme: () => void }) {
  const [searchValue, setSearchValue] = useState("");

  const users: User[] = [
    {
      name: "Rainer Zufall",
      balance: 51.4,
      enabled: true,
      role: UserRole.ADMINISTRATOR,
    },
    {
      name: "Erik Zion",
      balance: 3.2,
      enabled: true,
      role: UserRole.USER,
    },
    {
      name: "Kai Nepanik",
      balance: 25,
      enabled: true,
      role: UserRole.USER,
    },
    {
      name: "Frank Reich",
      balance: 2500,
      enabled: true,
      role: UserRole.USER,
    },
    {
      name: "Christian Harten",
      balance: -6.9,
      enabled: true,
      role: UserRole.USER,
    },
  ];

  return (
    <>
      <h1>
        <img onClick={props.switchTheme} src="/icons/happy-manje/happy beer.svg" />
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

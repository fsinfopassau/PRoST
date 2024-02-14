import { useState } from "react";
import UserContainer from "../UserSelection/UserContainer";
import { User } from "../../DTO/User";
import { UserRole } from "../../DTO/UserRole";

export function UserSelection() {
  const [searchValue, setSearchValue] = useState("");

  var users: User[] = [
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
        <img src="/icons/happy-manje/happy beer.svg" />
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

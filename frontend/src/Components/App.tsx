import "../styles/styles.css";
import "../styles/styles-own.css";
import { User } from "../DTO/User";
import UserContainer from "./UserContainer";
import { useState } from "react";
import { UserRole } from "../DTO/UserRole";

export function App() {
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

  const [searchValue, setSearchValue] = useState("");

  return (
    <>
      <h1>Kasse des Vertrauens</h1>
      <input type="search" onChange={(e) => setSearchValue(e.target.value)} />
      <div className="App">
        <UserContainer users={users} search={searchValue} />
      </div>
    </>
  );
}

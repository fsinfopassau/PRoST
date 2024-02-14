import "../styles/styles.css";
import "../styles/styles-own.css";
import { User } from "../DTO/User";
import UserContainer from "./UserContainer";
import { useState } from "react";

export function App() {
  const users: User[] = [
    {
      name: "Rainer Zufall",
      balance: 51.4,
      enabled: true,
      role: "ADMINISTRATOR",
    },
    {
      name: "Erik Zion",
      balance: 3.2,
      enabled: true,
      role: "USER",
    },
    {
      name: "Kai Nepanik",
      balance: 25,
      enabled: true,
      role: "USER",
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

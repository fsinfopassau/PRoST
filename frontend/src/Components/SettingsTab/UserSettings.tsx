import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { createNewUser, getAllUsers } from "../../Queries";
import { UserSettingCard } from "./UserSettingCard";
import ScrollDialog from "../Util/ScrollDialog";
import { toast } from "react-toastify";
import { PlusCircledIcon } from "@radix-ui/react-icons";

export function UserSettings() {
  const [users, setUsers] = useState<User[]>([]);
  const [newId, setNewId] = useState<string>("");
  const [newName, setNewName] = useState<string>("");
  const [newEmail, setNewEmail] = useState<string>("");

  useEffect(reloadUsers, []);

  function reloadUsers() {
    getAllUsers().then((users) => {
      if (users) setUsers(users);
    });
  }

  const handleIdChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewId(event.target.value);
  };
  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewName(event.target.value);
  };
  const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewEmail(event.target.value);
  };

  function createUser() {
    const user: User = {
      id: newId,
      displayName: newName,
      email: newEmail,
      balance: 0,
      enabled: true,
    };

    createNewUser(user).then((success) => {
      if (success) {
        toast.success("User " + newId + " created");
        reloadUsers();
      } else {
        toast.error("Master, you failed me!");
      }
    });
  }

  return (
    <>
      <ScrollDialog
        title="Neuer Nutzer"
        trigger={
          <div className="Button icon green">
            <div className="green">
              <PlusCircledIcon />
            </div>
          </div>
        }
        onSubmit={createUser}
      >
        <div
          style={{
            padding: "1rem",
            display: "flex",
            flexDirection: "column",
            gap: ".5rem",
          }}
        >
          <div className="DialogDescription">Identifier:</div>
          <fieldset className="Fieldset">
            <input
              className="Input"
              onChange={handleIdChange}
              placeholder={"sugmaW"}
            />
          </fieldset>
          <div className="DialogDescription">Name:</div>
          <fieldset className="Fieldset">
            <input
              className="Input"
              onChange={handleNameChange}
              placeholder={"Willy Wonka"}
            />
          </fieldset>
          <div className="DialogDescription">E-Mail:</div>
          <fieldset className="Fieldset">
            <input
              className="Input"
              onChange={handleEmailChange}
              placeholder={"mail@wonka.de"}
            />
          </fieldset>
        </div>
      </ScrollDialog>
      <div className="CardContainer">
        {users.map((user, index) => (
          <UserSettingCard user={user} key={index} onUpdate={reloadUsers} />
        ))}
      </div>
    </>
  );
}

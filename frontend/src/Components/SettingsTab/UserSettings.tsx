import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { createNewUser, getAllUsers } from "../../Queries";
import { UserSettingCard } from "./UserSettingCard";
import ScrollDialog from "../Util/ScrollDialog";
import { toast } from "react-toastify";
import { Pencil2Icon, PlusCircledIcon } from "@radix-ui/react-icons";
import Fuse from "fuse.js";
import { Separator } from "@radix-ui/react-separator";

export function UserSettings() {
  const [searchValue, setSearchValue] = useState("");
  const [users, setUsers] = useState<User[]>([]);
  const [newId, setNewId] = useState<string>("");
  const [newName, setNewName] = useState<string>("");
  const [newEmail, setNewEmail] = useState<string>("");
  const [editMode, setEditMode] = useState(false);

  useEffect(reloadUsers, []);

  function toggleEdit() {
    if (editMode) {
      setEditMode(false);
    } else {
      setEditMode(true);
    }
  }

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
      totalSpent: 0,
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

  function filter(users: User[]): User[] {
    if (searchValue.trim().length === 0) {
      return users.filter((user) => user.enabled === true);
    }

    const fuse = new Fuse(users, {
      keys: ["id", "displayName"],
    });
    const results = fuse.search(searchValue).map((result) => result.item);

    return results.filter((user) => user.enabled === true);
  }

  return (
    <>
      <div className="DisplayCard">
        <div style={{ display: "flex", gap: "1rem" }}>
          <input
            type="text"
            placeholder="Nutzer"
            className="Input"
            onChange={(e) => setSearchValue(e.target.value)}
          />
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
                  placeholder={"WillyD"}
                />
              </fieldset>
              <div className="DialogDescription">E-Mail:</div>
              <fieldset className="Fieldset">
                <input
                  className="Input"
                  onChange={handleEmailChange}
                  placeholder={"mail@willy.de"}
                />
              </fieldset>
            </div>
          </ScrollDialog>

          <div
            className={editMode ? "Button orange" : "Button"}
            onClick={toggleEdit}
          >
            <Pencil2Icon />
          </div>
        </div>
        <Separator className="Separator" />
        <div className="CardContainer">
          {filter(users).map((user, index) => (
            <UserSettingCard
              user={user}
              key={index}
              editMode={editMode}
              onUpdate={reloadUsers}
            />
          ))}
        </div>
      </div>
    </>
  );
}
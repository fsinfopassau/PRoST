import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { createNewUser, getAllUsers } from "../../Queries";
import { UserSettingCard } from "./UserSettingCard";
import ScrollDialog from "../Util/ScrollDialog";
import { toast } from "react-toastify";
import { Pencil2Icon, PlusCircledIcon } from "@radix-ui/react-icons";
import Fuse from "fuse.js";
import { Separator } from "@radix-ui/react-separator";
import { filterNameId, getValidMoney, isValidEmail, isValidString } from "../../Format";

export function UserSettings() {
  const [searchValue, setSearchValue] = useState("");
  const [users, setUsers] = useState<User[]>([]);
  const [newId, setNewId] = useState<string>("");
  const [newName, setNewName] = useState<string>("");
  const [newEmail, setNewEmail] = useState<string>("");
  const [moneySpent, setMoneySpent] = useState<string>("");
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
    getAllUsers(true).then((users) => {
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
  const handleMoneySpentChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setMoneySpent(event.target.value);
  };

  function createUser() {
    const user: User = {
      id: newId,
      displayName: newName,
      email: newEmail,
      balance: 0,
      enabled: true,
      hidden: false,
      totalSpent: 0,
    };

    const money = getValidMoney(moneySpent);

    if (money) {
      user.totalSpent = money;
    }

    createNewUser(user).then((success) => {
      if (success) {
        toast.success("User " + newId + " created");
        reloadUsers();
      } else {
        toast.error("Meister, warum entäuschst du mich? 😢");
      }
    });
  }

  function filter(users: User[]): User[] {
    if (searchValue.trim().length === 0) {
      return users;
    }

    const fuse = new Fuse(users, {
      keys: ["id", "displayName", "email"],
    });
    return fuse.search(searchValue).map((result) => result.item);
  }

  return (
    <>
      <div className="DisplayCard">
        <h2>Nutzer</h2>
        <div className="tableSearch">
          <input
            type="text"
            placeholder="Id, Name, Email"
            className="Input"
            onChange={(e) => setSearchValue(e.target.value)}
          />
          <ScrollDialog
            title="Neuer Nutzer"
            trigger={
              <div className="Button icon good-color">
                <div className="good-color">
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
              <div className="DialogDescription">Identifier: *</div>
              <fieldset className="Fieldset">
                <input
                  className={isValidString(filterNameId(newId)) ? "Input good-color" : "Input danger-color"}
                  onChange={handleIdChange}
                  placeholder={"sugmaW"}
                />
              </fieldset>
              <div className="DialogDescription">Name: *</div>
              <fieldset className="Fieldset">
                <input
                  className={isValidString(newName) ? "Input good-color" : "Input danger-color"}
                  onChange={handleNameChange}
                  placeholder={"WillyD"}
                />
              </fieldset>
              <div className="DialogDescription">E-Mail: *</div>
              <fieldset className="Fieldset">
                <input
                  className={isValidEmail(newEmail) ? "Input good-color" : "Input danger-color"}
                  onChange={handleEmailChange}
                  placeholder={"mail@willy.de"}
                />
              </fieldset>
              <div className="DialogDescription">Bisher ausgegeben:</div>
              <fieldset className="Fieldset">
                <input
                  className={
                    getValidMoney(moneySpent.trim().length === 0 ? "0" : moneySpent) !== undefined
                      ? "Input good-color"
                      : "Input danger-color"
                  }
                  onChange={handleMoneySpentChange}
                  placeholder={"0"}
                />
              </fieldset>
            </div>
          </ScrollDialog>

          <div className={editMode ? "Button icon important-color" : "Button icon"} onClick={toggleEdit}>
            <div>
              <Pencil2Icon />
            </div>
          </div>
        </div>
        <Separator className="Separator" />
        <div className="GridContainer">
          {filter(users).map((user, index) => (
            <UserSettingCard user={user} key={index} editMode={editMode} onUpdate={reloadUsers} />
          ))}
        </div>
      </div>
    </>
  );
}

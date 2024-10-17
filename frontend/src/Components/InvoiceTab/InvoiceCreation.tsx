import { PropsWithChildren, useEffect, useState } from "react";
import { CheckIcon } from "@radix-ui/react-icons";
import { User } from "../../Types/User";
import { createInvoices, getAllUsers } from "../../Queries";
import { InvoiceCreationUserDisplay } from "./InvoiceCreationUserDisplay";
import { Separator } from "@radix-ui/react-separator";
import { toast } from "react-toastify";
import ScrollDialog from "../Util/ScrollDialog";

interface CustomComponentProps {
  onSubmit: () => void;
}

const InvoiceCreation: React.FC<PropsWithChildren<CustomComponentProps>> = ({ children, onSubmit }) => {
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUsers, setSelectedUsers] = useState<string[]>([]);

  useEffect(() => {
    getAllUsers().then((userList) => {
      if (userList) {
        setUsers(userList);
      }
    });
  }, []);

  function handleSelect(id: string) {
    setSelectedUsers((prev) => (prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]));
  }

  function toggleAll() {
    if (isAllSelected()) {
      setSelectedUsers([]);
      return;
    }

    setSelectedUsers([]);
    users.forEach((i) => {
      if (i.enabled) handleSelect(i.id);
    });
  }

  function isAllSelected(): boolean {
    if (users === undefined || users.length === 0 || selectedUsers.length === 0) {
      return false;
    }
    return users.every((user) => {
      if (selectedUsers.includes(user.id)) {
        return true;
      } else if (user.balance === 0) {
        return true;
      } else if (!user.enabled) {
        return true;
      }
      return false;
    });
  }

  function create() {
    if (selectedUsers.length === 0) return;

    createInvoices(selectedUsers)
      .then((result) => {
        if (result && result.length !== undefined) {
          if (result.length !== 0) {
            toast.success(result.length + " neue Rechnung" + (result.length > 1 ? "en" : "") + "!");
            onSubmit();
          }
        } else {
          toast.error("Rechnungsfehler!");
        }
      })
      .catch(() => {
        toast.error("Verbindungsfehler!");
      });
    setSelectedUsers([]);
  }

  return (
    <>
      <ScrollDialog title="Neue Rechnungen" trigger={<div>{children} </div>} onSubmit={create}>
        <div className="SpreadContainer" style={{ padding: "0 .7rem" }}>
          {isAllSelected() ? (
            <div className="CheckBox good-color" onClick={toggleAll}>
              <CheckIcon />
            </div>
          ) : (
            <div className="CheckBox" onClick={toggleAll}></div>
          )}
          <div>{selectedUsers.length} Nutzer ausgewählt</div>
        </div>
        <Separator className="Separator" />
        <table className="Table">
          <tbody>
            {users.map((user) => (
              <InvoiceCreationUserDisplay
                key={user.id}
                user={user}
                selected={selectedUsers.includes(user.id)}
                onSelect={handleSelect}
              />
            ))}
          </tbody>
        </table>
      </ScrollDialog>
    </>
  );
};

export default InvoiceCreation;

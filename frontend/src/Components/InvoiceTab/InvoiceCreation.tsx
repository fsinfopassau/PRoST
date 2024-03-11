import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
  DialogTrigger,
} from "@radix-ui/react-dialog";
import { PropsWithChildren, useEffect, useState } from "react";
import { CheckIcon, Cross2Icon } from "@radix-ui/react-icons";
import { User } from "../../Types/User";
import { createInvoices, getAllUsers } from "../../Queries";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";
import { InvoiceCreationUserDisplay } from "./InvoiceCreationUserDisplay";
import { Separator } from "@radix-ui/react-separator";
import { toast } from "react-toastify";

interface CustomComponentProps {
  onSubmit: () => void;
}

const InvoiceCreation: React.FC<PropsWithChildren<CustomComponentProps>> = ({
  children,
  onSubmit,
}) => {
  const [open, setOpen] = useState(false);
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUsers, setSelectedUsers] = useState<string[]>([]);

  useEffect(() => {
    getAllUsers().then((userList) => {
      setUsers(userList);
    });
  }, []);

  function handleSelect(id: string) {
    console.log("select " + id);

    setSelectedUsers((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  }

  function toggleAll() {
    if (isAllSelected()) {
      setSelectedUsers([]);
      return;
    }

    setSelectedUsers([]);
    users.forEach((i) => {
      if (i.balance !== 0 && i.enabled) handleSelect(i.id);
    });
  }

  function isAllSelected(): boolean {
    if (
      users === undefined ||
      users.length === 0 ||
      selectedUsers.length === 0
    ) {
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

    createInvoices(selectedUsers).then((result) => {
      if (result) {
        if (result.length !== 0) {
          toast.success(
            result.length +
              " neue Rechnung" +
              (result.length > 1 ? "en" : "") +
              "!"
          );
          onSubmit();
        }
      } else {
        toast.error("Rechnungsfehler!");
      }
    });
    setSelectedUsers([]);
  }

  function handleKeyDown(event: React.KeyboardEvent) {
    if (event.key === "Enter") {
      create();
      setOpen(false);
    }
    if (event.key === "Escape") {
      setOpen(false);
    }
  }

  return (
    <>
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogTrigger asChild>
          <div>{children}</div>
        </DialogTrigger>
        <DialogPortal>
          <DialogOverlay className="DialogOverlay" />
          <DialogContent className="DialogContent" onKeyDown={handleKeyDown}>
            <div>
              <DialogClose asChild className="DialogClose">
                <button className="IconButton" aria-label="Close">
                  <Cross2Icon />
                </button>
              </DialogClose>
            </div>
            <DialogTitle className="DialogTitle">Neue Abrechnungen</DialogTitle>
            <div className="DialogDescription">
              <ScrollArea
                style={{
                  height: "100%",
                  maxWidth: "80rem",
                }}
              >
                <ScrollAreaViewport
                  style={{
                    maxHeight: "15rem",
                    minHeight: "50vh",
                    height: "50%",
                    width: "100%",
                  }}
                >
                  <div
                    className="SpreadContainer"
                    style={{ padding: "0 .5rem" }}
                  >
                    {isAllSelected() ? (
                      <div className="Toggle green" onClick={toggleAll}>
                        <CheckIcon />
                      </div>
                    ) : (
                      <div className="Toggle" onClick={toggleAll}></div>
                    )}
                    <div>{selectedUsers.length} Nutzer ausgewählt</div>
                  </div>
                  <Separator className="Separator" />
                  <table className="InvoiceTable">
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
                </ScrollAreaViewport>
                <ScrollAreaScrollbar
                  className="Scrollbar"
                  orientation="vertical"
                >
                  <ScrollAreaThumb className="ScrollbarThumb" />
                </ScrollAreaScrollbar>
                <ScrollAreaScrollbar
                  className="Scrollbar"
                  orientation="horizontal"
                >
                  <ScrollAreaThumb className="ScrollbarThumb" />
                </ScrollAreaScrollbar>
              </ScrollArea>
            </div>
            <div
              style={{
                display: "flex",
                marginTop: 25,
                justifyContent: "flex-end",
              }}
            >
              <DialogClose asChild onClick={create}>
                <button className="Button">
                  <CheckIcon />
                </button>
              </DialogClose>
            </div>
          </DialogContent>
        </DialogPortal>
      </Dialog>
    </>
  );
};

export default InvoiceCreation;

import { toast } from "react-toastify";
import { User } from "../../Types/User";
import { Switch, SwitchThumb } from "@radix-ui/react-switch";
import {
  ChatBubbleIcon,
  CheckCircledIcon,
  Cross1Icon,
  CrossCircledIcon,
  EnvelopeClosedIcon,
  LightningBoltIcon,
  PlusIcon,
} from "@radix-ui/react-icons";
import { Separator } from "@radix-ui/react-separator";
import { ButtonDialogChanger } from "../Util/ButtonDialogChange";
import { formatMoney } from "../../Format";
import { changeUser, deleteUser, enableUser } from "../../Queries";
import ScrollDialog from "../Util/ScrollDialog";
import { UserSummaryCard } from "../StatisticsTab/UserSummaryCard";
import { Link } from "react-router-dom";
import { useState } from "react";

export function UserSettingCard(props: {
  user: User;
  editMode: boolean;
  onUpdate: () => void;
}) {
  const { user, editMode, onUpdate } = props;

  const [transactionAmount, setTransactionAmount] = useState<string>("");

  function applyTransaction() {
    console.log(transactionAmount);
    changeUser(user, transactionAmount, "transaction").then((result) => {
      if (result) {
        toast.success(
          formatMoney(Math.abs(Number(transactionAmount))) +
            ' wurde "' +
            user.id +
            '" gutgeschrieben!'
        );
      } else {
        toast.error("Transaktion fehlgeschlagen!");
      }
    });
    setTransactionAmount("");
    onUpdate();
  }

  function handleKeyDown(event: React.KeyboardEvent) {
    if (event.key === "Enter") {
      applyTransaction();
    }
  }

  function toggleEnable() {
    const newVal = !user.enabled;
    enableUser(user, newVal).then((result) => {
      if (result) {
        toast.success(user.id + (newVal ? " aktiviert!" : " deaktiviert!"));
        onUpdate();
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  function deleteItem() {
    deleteUser(user).then((result) => {
      if (result) {
        toast.warning(user.id + " gelöscht!");
        onUpdate();
      } else {
        toast.error("Löschen fehlgeschlagen!");
      }
    });
  }

  function setName(newName: string) {
    changeUser(user, newName, "name").then((result) => {
      if (result) {
        toast.success("Name geändert.");
        onUpdate();
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  function setMail(newMail: string) {
    changeUser(user, newMail, "email").then((result) => {
      if (result) {
        toast.success("Mail geändert.");
        onUpdate();
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  function setBalance(newBalance: string) {
    changeUser(user, newBalance, "balance").then((result) => {
      if (result) {
        toast.success("Kontostand geändert.");
        onUpdate();
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  return (
    <div className="DisplayCard">
      <div className="SpreadContainer">
        <h3>
          {!editMode ? (
            <Link
              className="bold SpreadContainer"
              to={`/stats/users/${user.id}`}
            >
              {user.displayName}
            </Link>
          ) : (
            <h3 className="SpreadContainer">
              <Link className="bold" to={`/stats/users/${user.id}`}>
                {user.id}
              </Link>
              <Switch
                className="SwitchRoot"
                defaultChecked={user.enabled}
                onCheckedChange={toggleEnable}
              >
                <SwitchThumb className="SwitchThumb" />
              </Switch>
            </h3>
          )}
        </h3>

        <div className="SpreadContainer">
          {!editMode ? (
            <></>
          ) : (
            <ScrollDialog
              title="Nutzer löschen?"
              onSubmit={deleteItem}
              trigger={
                <div className="Button red">
                  <Cross1Icon />
                </div>
              }
            >
              <h3 style={{ display: "flex" }}>
                {user.id}
                {user.enabled ? (
                  <div className="green">
                    <CheckCircledIcon />
                  </div>
                ) : (
                  <div className="red">
                    <CrossCircledIcon />
                  </div>
                )}
              </h3>
              <UserSummaryCard user={user} />
              <div className="DisplayCard">
                <div style={{ display: "flex", alignItems: "center" }}>
                  <EnvelopeClosedIcon />
                  {user.email}
                </div>
              </div>
            </ScrollDialog>
          )}
        </div>
      </div>
      <Separator className="Separator" />
      {!editMode ? (
        <>
          <h3>{formatMoney(user.balance)}</h3>
          <div className="SpreadContainer">
            <input
              type="text"
              placeholder="Betrag"
              className="Input"
              value={transactionAmount}
              onKeyDown={handleKeyDown}
              onChange={(e) => setTransactionAmount(e.target.value)}
            />
            <div className="Button green" onClick={applyTransaction}>
              <PlusIcon />
            </div>
          </div>
        </>
      ) : (
        <div className="SelectionContainer">
          <div className="Button">
            <ChatBubbleIcon />
            <ButtonDialogChanger
              name={user.displayName}
              dialogName="Namen ändern"
              dialogDesc="Ändere hier den Namen des Nutzers"
              onSubmit={setName}
              key={"NameChanger"}
            />
          </div>
          <div className="Button">
            <EnvelopeClosedIcon />
            <ButtonDialogChanger
              name={user.email}
              dialogName="E-Mail ändern"
              dialogDesc="Ändere hier die E-Mail adresse des Nutzers"
              onSubmit={setMail}
              key={"MailChanger"}
            />
          </div>
          <div className="Button">
            <LightningBoltIcon />
            <ButtonDialogChanger
              name={formatMoney(user.balance)}
              dialogName="Kontostand ändern"
              dialogDesc="Ändere hier den Kontostand des Nutzers"
              onSubmit={setBalance}
              key={"BalanceChanger"}
            />
          </div>
        </div>
      )}
    </div>
  );
}

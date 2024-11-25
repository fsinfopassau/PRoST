import { toast } from "react-toastify";
import { getLevel, User } from "../../Types/User";
import { Switch, SwitchThumb } from "@radix-ui/react-switch";
import {
  ChatBubbleIcon,
  CheckCircledIcon,
  CheckIcon,
  CookieIcon,
  Cross1Icon,
  Cross2Icon,
  CrossCircledIcon,
  DotIcon,
  DoubleArrowRightIcon,
  EnvelopeClosedIcon,
  EyeClosedIcon,
  EyeOpenIcon,
  LightningBoltIcon,
  PlusIcon,
  RocketIcon,
} from "@radix-ui/react-icons";
import { Separator } from "@radix-ui/react-separator";
import { ButtonDialogChanger } from "../Util/ButtonDialogChange";
import { formatMoney, getValidMoney } from "../../Format";
import { changeUser, createTransaction, deleteUser, enableUser, hideUser, setUserKiosk } from "../../Queries";
import ScrollDialog from "../Util/ScrollDialog";
import { UserSummaryCard } from "../StatisticsTab/UserSummaryCard";
import { Link } from "react-router-dom";
import { useState } from "react";

export function UserSettingCard(props: { user: User; editMode: boolean; onUpdate: () => void }) {
  const { user, editMode, onUpdate } = props;

  const [transactionAmount, setTransactionAmount] = useState<string>("");

  function applyTransaction() {
    const money = getValidMoney(transactionAmount);
    if (money) {
      createTransaction(user, money.toString(), "deposit").then((result) => {
        if (result) {
          toast.success(formatMoney(Math.abs(money)) + ' wurde "' + user.id + '" gutgeschrieben!');
          setTransactionAmount("");
          onUpdate();
        } else {
          toast.error("Transaktion fehlgeschlagen!");
        }
      });
    } else {
      toast.error("Kein gültiger Betrag!");
    }
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

  function toggleHidden() {
    const newVal = !user.hidden;
    hideUser(user, newVal).then((result) => {
      if (result) {
        toast.success(user.id + (newVal ? " ist jetzt versteckt!" : " ist jetzt öffentlich!"));
        onUpdate();
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  function toggleKiosk() {
    const newVal = !user.kiosk;
    setUserKiosk(user, newVal).then((result) => {
      if (result) {
        toast.success(
          newVal ? "Der Kiosk ist mit diesem Profil nutzbar!" : "Das Profil kann den Kiosk nichtmehr nutzen!"
        );
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

  function setTotalSpent(spentTotal: string) {
    const money = getValidMoney(spentTotal);
    if (money != undefined) {
      changeUser(user, money.toString(), "moneySpent").then((result) => {
        if (result) {
          toast.success("Gesamtausgaben geändert.");
          onUpdate();
        } else {
          toast.error("Änderung der Gesamtausgaben fehlgeschlagen!");
        }
      });
    } else {
      toast.error("Kein gültiger Betrag!");
    }
  }

  function setBalance(newBalance: string) {
    const money = getValidMoney(newBalance);
    if (money != undefined) {
      createTransaction(user, money.toString(), "change").then((result) => {
        if (result) {
          toast.success("Kontostand geändert.");
          onUpdate();
        } else {
          toast.error("Änderung fehlgeschlagen!");
        }
      });
    } else {
      toast.error("Kein gültiger Betrag!");
    }
  }

  function isTransactionAmountValid() {
    const money = getValidMoney(transactionAmount);

    if (transactionAmount.length === 0) return false;

    if (money && money > 0 && money <= 50) {
      return true;
    }
  }

  function NormalCard() {
    return (
      <div className="DisplayCard">
        <div className="SpreadContainer">
          <h3 className="SpreadContainer">
            <Link className="bold SpreadContainer" to={`/stats/users/${user.id}`}>
              {user.displayName}
              <DotIcon /> {user.id}
            </Link>
          </h3>
          <h3>
            <RocketIcon />
            {getLevel(user)}
          </h3>
        </div>

        <div style={{ display: "flex", gap: "1rem", padding: "0.25rem 0" }}>
          {user.enabled ? (
            <div className="good-color">
              <CheckIcon />
            </div>
          ) : (
            <div className="danger-color">
              <Cross2Icon />
            </div>
          )}
          {user.hidden ? (
            <div className="danger-color">
              <EyeClosedIcon />
            </div>
          ) : (
            <div className="good-color">
              <EyeOpenIcon />
            </div>
          )}
          {user.kiosk ? (
            <div className="good-color">
              <CookieIcon />
            </div>
          ) : (
            <div className="danger-color">
              <CookieIcon />
            </div>
          )}
        </div>

        <Separator className="Separator" />

        <h3>{formatMoney(user.balance)}</h3>
        <div className="SpreadContainer">
          <input
            type="text"
            placeholder="Betrag"
            className={
              transactionAmount.length === 0
                ? "Input"
                : isTransactionAmountValid()
                ? "Input good-color"
                : "Input danger-color"
            }
            value={transactionAmount}
            onKeyDown={handleKeyDown}
            onChange={(e) => setTransactionAmount(e.target.value)}
          />
          <div className="Button good-color icon" onClick={applyTransaction}>
            <PlusIcon />
          </div>
        </div>
      </div>
    );
  }

  function EditCard() {
    return (
      <div className="DisplayCard">
        <div className="SpreadContainer">
          <h3 className="SpreadContainer">
            <ButtonDialogChanger
              dialogName="Namen ändern"
              dialogDesc="Ändere hier den Namen des Nutzers"
              onSubmit={setName}
              key={"NameChanger"}
              trigger={
                <div className="Button">
                  <ChatBubbleIcon />
                  {user.displayName}
                </div>
              }
            />
            <DotIcon />
            <Link className="bold" to={`/stats/users/${user.id}`}>
              {user.id}
            </Link>
          </h3>

          <div className="SpreadContainer">
            <ScrollDialog
              title="Nutzer löschen?"
              onSubmit={deleteItem}
              trigger={
                <div className="Button icon danger-color">
                  <Cross1Icon />
                </div>
              }
            >
              <h3 style={{ display: "flex" }}>
                {user.id}
                {user.enabled ? (
                  <div className="good-color">
                    <CheckCircledIcon />
                  </div>
                ) : (
                  <div className="danger-color">
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
          </div>
        </div>
        <div style={{ display: "flex", gap: "1rem", padding: "0.25rem 0", alignItems: "center" }}>
          <Switch className="SwitchRoot" defaultChecked={user.enabled} onCheckedChange={toggleEnable}>
            <SwitchThumb className="SwitchThumb" />
          </Switch>

          {user.hidden ? (
            <div className="Button icon danger-color" onClick={toggleHidden}>
              <EyeClosedIcon />
            </div>
          ) : (
            <div className="Button icon good-color" onClick={toggleHidden}>
              <EyeOpenIcon />
            </div>
          )}

          {user.kiosk ? (
            <div className="Button icon good-color" onClick={toggleKiosk}>
              <CookieIcon />
            </div>
          ) : (
            <div className="Button icon danger-color" onClick={toggleKiosk}>
              <CookieIcon />
            </div>
          )}
        </div>
        <Separator className="Separator" />
        <div className="SmallGridContainer">
          <ButtonDialogChanger
            dialogName="Gesamtausgaben"
            dialogDesc="Ändere hier die Gesamtausgaben des Nutzers"
            onSubmit={setTotalSpent}
            key={"Totalspent"}
            trigger={
              <div className="Button">
                <RocketIcon />
                {getLevel(user)}
                <DoubleArrowRightIcon />
                {formatMoney(user.totalSpent)}
              </div>
            }
          />
          <ButtonDialogChanger
            dialogName="E-Mail ändern"
            dialogDesc="Ändere hier die E-Mail adresse des Nutzers"
            onSubmit={setMail}
            key={"MailChanger"}
            trigger={
              <div className="Button">
                <EnvelopeClosedIcon />
                {user.email}
              </div>
            }
          />
          <ButtonDialogChanger
            dialogName="Kontostand ändern"
            dialogDesc="Ändere hier den Kontostand des Nutzers"
            onSubmit={setBalance}
            key={"BalanceChanger"}
            trigger={
              <div className="Button">
                <LightningBoltIcon />
                {formatMoney(user.balance)}
              </div>
            }
          />
        </div>
      </div>
    );
  }

  return editMode ? EditCard() : NormalCard();
}

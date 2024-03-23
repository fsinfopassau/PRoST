import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
  DialogTrigger,
} from "@radix-ui/react-dialog";
import {
  AvatarIcon,
  Cross2Icon,
  ExitIcon,
  PaperPlaneIcon,
} from "@radix-ui/react-icons";
import { useEffect, useState } from "react";
import { loginNew } from "../../Queries";
import {
  getSessionUserName,
  resetSession,
  existCredentials,
} from "../../SessionInfo";
import { toast } from "react-toastify";

export function LoginDialog() {
  const [userName, setUserName] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [isLogged, setLogged] = useState(existCredentials());
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const name = getSessionUserName();
    if (name) setUserName(name);
  }, []);

  const handleUserChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setUserName(event.target.value);
  };
  const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(event.target.value);
  };

  function submit() {
    loginNew(userName, password)
      .then((result) => {
        if (result !== undefined) {
          toast.success("Hallo " + result.displayName);
          setLogged(true);
          window.location.reload();
        } else {
          toast.error("Login fehlgeschlagen!");
          setLogged(false);
        }
      })
      .catch(() => {
        toast.error("Login Error!");
        setLogged(false);
      });
  }

  function logout() {
    toast.success("Tschüss " + userName);
    setUserName("");
    setPassword("");
    resetSession();
    setLogged(false);
    window.location.reload();
  }

  function handleKeyDown(event: React.KeyboardEvent) {
    if (event.key === "Enter") {
      submit();
      setOpen(false);
    }
  }

  return (
    <>
      <div id="login">
        {isLogged ? (
          <button onClick={logout} id="login-button">
            <div>{userName}</div>
            <ExitIcon width={35} height={25} />
          </button>
        ) : (
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <button id="login-button">
                <AvatarIcon width={35} height={25} />
              </button>
            </DialogTrigger>
            <DialogPortal>
              <DialogOverlay className="DialogOverlay" />
              <DialogContent className="DialogContent">
                <div>
                  <DialogClose asChild>
                    <button className="IconButton" aria-label="Close">
                      <Cross2Icon />
                    </button>
                  </DialogClose>
                </div>
                <DialogTitle className="DialogTitle">Login</DialogTitle>
                <DialogDescription className="DialogDescription">
                  Username
                </DialogDescription>
                <fieldset className="Fieldset">
                  <input
                    className="Input"
                    onChange={handleUserChange}
                    onKeyDown={handleKeyDown}
                  />
                </fieldset>
                <DialogDescription className="DialogDescription">
                  Password
                </DialogDescription>
                <fieldset className="Fieldset">
                  <input
                    className="Input"
                    onChange={handlePasswordChange}
                    id="newPassword"
                    type="password"
                    onKeyDown={handleKeyDown}
                  />
                </fieldset>

                <div
                  style={{
                    display: "flex",
                    marginTop: 25,
                    justifyContent: "flex-end",
                  }}
                >
                  <button className="Button" onClick={submit}>
                    <PaperPlaneIcon />
                  </button>
                </div>
              </DialogContent>
            </DialogPortal>
          </Dialog>
        )}
      </div>
    </>
  );
}

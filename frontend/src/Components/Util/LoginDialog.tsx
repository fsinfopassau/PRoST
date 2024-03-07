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
  Cross2Icon,
  ExitIcon,
  PaperPlaneIcon,
  PersonIcon,
} from "@radix-ui/react-icons";
import { useState } from "react";
import { loginNew } from "./Queries";
import { resetCredentials, validCredentials } from "./SessionInfo";

export function LoginDialog() {
  const [userName, setUserName] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [isLogged, setLogged] = useState(validCredentials());
  const [open, setOpen] = useState(false);

  const handleUserChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setUserName(event.target.value);
  };
  const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(event.target.value);
  };

  function buttonClickHandler() {
    console.log("log");
  }

  function submit() {
    loginNew(userName, password)
      .then((result) => {
        console.log(
          "login with " +
            userName +
            ":" +
            password +
            " " +
            (result ? "success" : "false")
        );
        setLogged(result);
      })
      .catch(() => {
        setLogged(false);
      });
  }

  function logout() {
    setUserName("");
    setPassword("");
    resetCredentials();
    setLogged(false);
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
          <div>
            <button onClick={logout} className="Button">
              <ExitIcon />
            </button>
          </div>
        ) : (
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <div>
                <button onClick={buttonClickHandler} className="Button">
                  <PersonIcon />
                </button>
              </div>
            </DialogTrigger>
            <DialogPortal>
              <DialogOverlay className="DialogOverlay" />
              <DialogContent className="DialogContent">
                <div>
                  <DialogClose asChild className="DialogClose">
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
                  <DialogClose asChild onClick={submit}>
                    <button className="Button">
                      <PaperPlaneIcon />
                    </button>
                  </DialogClose>
                </div>
              </DialogContent>
            </DialogPortal>
          </Dialog>
        )}
      </div>
    </>
  );
}

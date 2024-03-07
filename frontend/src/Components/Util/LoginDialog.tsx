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
    resetCredentials();
    setLogged(false);
  }

  return (
    <>
      <div id="login">
        <div className="Button">
          {isLogged ? (
            <button onClick={logout}>
              <ExitIcon />
            </button>
          ) : (
            <Dialog>
              <DialogTrigger asChild>
                <button onClick={buttonClickHandler}>
                  <PersonIcon />
                </button>
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
                    <input className="Input" onChange={handleUserChange} />
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
      </div>
    </>
  );
}

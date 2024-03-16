import { PaperPlaneIcon } from "@radix-ui/react-icons";
import { useEffect, useState } from "react";
import { getSessionUserName } from "../../SessionInfo";
import { loginNew } from "../../Queries";
import { toast } from "react-toastify";

export function Login() {
  const [userName, setUserName] = useState<string>("");
  const [password, setPassword] = useState<string>("");

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
          window.location.reload();
        } else {
          toast.error("Login fehlgeschlagen!");
        }
      })
      .catch(() => {
        toast.error("Login Error!");
      });
  }

  function handleKeyDown(event: React.KeyboardEvent) {
    if (event.key === "Enter") {
      submit();
    }
  }
  return (
    <>
      <div className="DisplayCard">
        <h1>Login</h1>
        <span className="DialogDescription">Username</span>
        <fieldset className="Fieldset">
          <input
            className="Input"
            onChange={handleUserChange}
            onKeyDown={handleKeyDown}
          />
        </fieldset>
        <span className="DialogDescription">Password</span>
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
      </div>
    </>
  );
}

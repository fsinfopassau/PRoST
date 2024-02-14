import { Label } from "@radix-ui/react-label";
import { User } from "../DTO/User";

export function UserBox(user: User) {
  function selectUser(user: User) {
    console.log(user.name + " has been selected");
  }

  return (
    <button className="Button violet large" onClick={() => selectUser(user)}>
      <Label className="bold" htmlFor="username">
        {user.name}
      </Label>
    </button>
  );
}

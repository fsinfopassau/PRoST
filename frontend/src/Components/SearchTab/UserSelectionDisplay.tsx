import { Label } from "@radix-ui/react-label";
import { User } from "../../Types/User";
import { useNavigate } from "react-router-dom";

export function UserBox(props: { user: User }) {
  const navigate = useNavigate();
  function selectUser(user: User) {
    navigate(`/shop/${user.username}`);
  }

  return (
    <button className="Button large" onClick={() => selectUser(props.user)}>
      <Label className="bold" htmlFor="username">
        {props.user.username}
      </Label>
    </button>
  );
}

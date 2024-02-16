import { Label } from "@radix-ui/react-label";
import { User } from "../../Types/User";
import { useNavigate } from "react-router-dom";

export function UserBox(props: {user: User}) {
  const navigate = useNavigate();
  function selectUser(user: User) {
    console.log(user.name + " has been selected");
    navigate(`/shop/${user.name}`)
  }

  return (
    <button className="Button theme large" onClick={() => selectUser(props.user)}>
      <Label className="bold" htmlFor="username">
        {props.user.name}
      </Label>
    </button>
  );
}

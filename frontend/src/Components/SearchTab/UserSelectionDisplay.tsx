import { User } from "../../Types/User";
import { useNavigate } from "react-router-dom";

export function UserBox(props: { user: User }) {
  const { user } = props;

  const navigate = useNavigate();
  function selectUser(user: User) {
    navigate(`/shop/${user.id}`);
  }

  function getDisplayName(): string {
    return user.displayName ? user.displayName : user.id;
  }

  return (
    <button className="Button large bold" onClick={() => selectUser(props.user)}>
      {getDisplayName()}
    </button>
  );
}

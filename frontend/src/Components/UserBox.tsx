import { User, formatBalance } from "../DTO/User";

export function UserBox(user: User) {
  function selectUser(user: User) {
    console.log(user.name + " has been selected");
  }

  return (
    <button className="user-box" onClick={() => selectUser(user)}>
      <p>{user.name}</p>
      <small>{formatBalance(user.balance)}</small>
    </button>
  );
}

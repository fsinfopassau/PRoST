import { User } from "../DTO/User";

export function userBalanceString(user: User): string {
  return user.balance + "";
}

function formatBalance(balance: number, decimalCount = 2): string {
  const formatted = new Intl.NumberFormat("de-DE", {
    minimumFractionDigits: decimalCount,
    maximumFractionDigits: decimalCount,
  }).format(balance);

  return formatted + " €";
}

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

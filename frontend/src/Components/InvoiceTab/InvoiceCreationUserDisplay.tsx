import { CheckIcon, LockClosedIcon, MinusIcon } from "@radix-ui/react-icons";
import { User } from "../../Types/User";
import { Link } from "react-router-dom";
import { formatMoney } from "../../Format";

export function InvoiceCreationUserDisplay(props: {
  user: User;
  selected: boolean;
  onSelect: (id: string) => void;
}) {
  const { user, selected, onSelect } = props;

  return (
    <>
      <tr className="table-entry">
        <th className="icon left">
          {!user.enabled ? (
            <div>
              <LockClosedIcon />
            </div>
          ) : user.balance === 0 ? (
            <div>
              <MinusIcon />
            </div>
          ) : selected ? (
            <div
              className="CheckBox green"
              onClick={() => {
                onSelect(user.id);
              }}
            >
              <CheckIcon />
            </div>
          ) : (
            <div
              className="CheckBox"
              onClick={() => {
                onSelect(user.id);
              }}
            ></div>
          )}
        </th>
        <th className="bold name">
          <Link to={`/stats/users/${user.id}`} className="bold">
            {user.displayName}
          </Link>
        </th>
        <th className="balance bold right">{formatMoney(user.balance)}</th>
      </tr>
    </>
  );
}

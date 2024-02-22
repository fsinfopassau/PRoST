import { Link } from "react-router-dom";

export function Settings() {
  return (
    <div className="CardContainer">
      <div className="DisplayCard SelectionContainer">
        <div className="Button">
          <Link to={"/settings/items"}>Items</Link>
        </div>
        <div className="Button">
          <Link to={"/settings/users"}>Benutzer</Link>
        </div>
      </div>
      <div className="DisplayCard">Settings TODO</div>
    </div>
  );
}

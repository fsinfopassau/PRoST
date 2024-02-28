import { Link } from "react-router-dom";

export function Settings() {
  return (
    <div className="CardContainer">
      <div className="DisplayCard">
        <div className="SelectionContainer">
          <Link to={"/settings/items"} className="Button">
            Items
          </Link>
          <Link to={"/settings/users"} className="Button">
            Benutzer
          </Link>
        </div>
      </div>
      <div className="DisplayCard">Settings TODO</div>
    </div>
  );
}

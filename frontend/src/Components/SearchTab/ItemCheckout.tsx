import { useNavigate, useParams } from "react-router-dom";
import { getUser } from "../Util/Queries";

export function ItemCheckout() {
  const { userid, itemid } = useParams();
  const navigate = useNavigate();

  function checkout() {
    console.log("checkout", userid, itemid);
    getUser(`${userid}`).then((user) => {
      console.log(user.name, user.balance);
      // TESTING
    });
    navigate("/");
  }

  return (
    <>
      <h1>{itemid}</h1>
      <button className="Button theme" onClick={checkout}>
        Fertig
      </button>
    </>
  );
}

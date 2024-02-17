import { useNavigate, useParams } from "react-router-dom";
import { buyItem, getShopItem } from "../Util/Queries";
import { ShopItem } from "../../Types/ShopItem";
import { useEffect, useState } from "react";
import { formatBalance } from "../../Types/User";

export function ItemCheckout() {
  const { userid, itemid } = useParams();
  const navigate = useNavigate();
  const [item, setItem] = useState<ShopItem>();

  if (itemid) {
    useEffect(() => {
      getShopItem(itemid).then((newItem) => {
        setItem(newItem);
      });
    }, []);
  }

  function checkout() {
    console.log("checkout", userid, itemid);
    if (userid && itemid)
      buyItem(userid, itemid, 1).then((balance) => {
        console.log("new Balance:", balance);
      });

    navigate("/");
  }

  function getPrice(): string {
    if (item) {
      return formatBalance(item.price);
    }
    return formatBalance(0);
  }

  return (
    <>
      <h1>{itemid}</h1>
      <p> Preis: {getPrice()}</p>
      <button className="Button theme" onClick={checkout}>
        Fertig
      </button>
    </>
  );
}

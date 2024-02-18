import { useNavigate, useParams } from "react-router-dom";
import { buyItem, getShopItem } from "../Util/Queries";
import { ShopItem } from "../../Types/ShopItem";
import { useEffect, useState } from "react";
import { formatMoney } from "../../Types/User";

export function ItemCheckout() {
  const { userId, itemId } = useParams();
  const navigate = useNavigate();
  const [item, setItem] = useState<ShopItem>();

  if (itemId) {
    useEffect(() => {
      getShopItem(itemId).then((newItem) => {
        setItem(newItem);
      });
    }, []);
  }

  function checkout() {
    console.log("checkout", userId, itemId);
    if (userId && itemId)
      buyItem(userId, itemId, 1).then((balance) => {
        console.log("new Balance:", balance);
      });

    navigate("/");
  }

  function getPrice(): string {
    if (item) {
      return formatMoney(item.price);
    }
    return formatMoney(0);
  }

  return (
    <>
      <h2>{itemId}</h2>
      <p> Preis: {getPrice()}</p>
      <button className="Button" onClick={checkout}>
        Fertig
      </button>
    </>
  );
}

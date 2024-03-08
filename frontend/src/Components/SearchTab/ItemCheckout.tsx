import { useNavigate, useParams } from "react-router-dom";
import { buyItem, getShopItem } from "../Util/Queries";
import { ShopItem } from "../../Types/ShopItem";
import { useEffect, useState } from "react";
import { formatMoney } from "../../Types/User";
import { toast } from "react-toastify";

export function ItemCheckout() {
  const { userId, itemId } = useParams();
  const [item, setItem] = useState<ShopItem>();
  const navigate = useNavigate();

  useEffect(() => {
    if (itemId) {
      getShopItem(itemId).then((newItem) => {
        setItem(newItem);
      });
    }
  }, []);

  function checkout() {
    console.log("checkout", userId, itemId);
    if (userId && itemId) {
      buyItem(userId, itemId, 1).then((result) => {
        if (result) {
          toast(item?.displayName + " gekauft!");
        } else {
          toast.error(item?.displayName + " konnte nicht gekauft werden!");
        }
      });
    }
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
      <h2>{item?.displayName}</h2>
      <p> Preis: {getPrice()}</p>
      <button className="Button" onClick={checkout}>
        Fertig
      </button>
    </>
  );
}

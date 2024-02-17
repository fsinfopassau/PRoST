import { useParams } from "react-router-dom";
import { ShopItem } from "../../Types/ShopItem";
import { ItemContainer } from "./ItemContainer";
import { useEffect, useState } from "react";
import { getAllShopItems, getUser } from "../Util/Queries";
import { User, formatBalance } from "../../Types/User";

export function ItemSelection() {
  const { userid } = useParams();
  const [items, setItems] = useState<ShopItem[]>([]);
  const [user, setUser] = useState<User>();

  useEffect(() => {
    getAllShopItems().then((itemList) => {
      setItems(itemList);
    });
    if (userid)
      getUser(userid).then((user) => {
        setUser(user);
      });
  }, []);

  function getBalance(): string {
    if (user?.balance) {
      return formatBalance(user?.balance);
    }
    return formatBalance(0);
  }

  return (
    <>
      <h1>{userid}</h1>
      <p> ({getBalance()})</p>
      <ItemContainer items={items} />
    </>
  );
}

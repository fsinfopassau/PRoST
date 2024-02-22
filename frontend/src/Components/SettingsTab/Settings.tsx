import { useEffect, useState } from "react";
import { getAllShopItems } from "../Util/Queries";
import { ShopItem } from "../../Types/ShopItem";
import { ItemSetting } from "./ItemSetting";

export function Settings() {
  const [items, setItems] = useState<ShopItem[]>([]);

  useEffect(() => {
    getAllShopItems().then((itemList) => {
      setItems(itemList);
    });
  }, []);

  return (
    <div className="CardContainer">
      <div className="DisplayCard">
        {items.map((item, index) => (
          <ItemSetting item={item} key={index}/>
        ))}
      </div>
      <div className="DisplayCard">Settings TODO</div>
    </div>
  );
}

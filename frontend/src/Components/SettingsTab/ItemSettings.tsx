import { useEffect, useState } from "react";
import { getAllShopItems } from "../../Queries";
import { ShopItem } from "../../Types/ShopItem";
import { ItemSettingCard } from "./ItemSettingCard";

export function ItemSettings() {
  const [items, setItems] = useState<ShopItem[]>([]);

  useEffect(reloadShopItems, []);

  function reloadShopItems() {
    getAllShopItems().then((itemList) => {
      if (itemList) setItems(itemList);
    });
  }

  return (
    <div className="CardContainer">
      {items.map((item, index) => (
        <ItemSettingCard item={item} key={index} onUpdate={reloadShopItems} />
      ))}
    </div>
  );
}

import { useEffect, useState } from "react";
import { getAllShopItems } from "../../Queries";
import { ShopItem } from "../../Types/ShopItem";
import { ItemDisplay } from "../SearchTab/ItemDisplay";

export function AllItemStatistics() {
  const [items, setItems] = useState<ShopItem[]>([]);

  useEffect(() => {
    getAllShopItems()
      .then((itemList) => {
        if (itemList === undefined) {
          setItems([]);
        } else {
          setItems(itemList);
        }
      })
      .catch(() => {
        setItems([]);
      });
  }, []);

  return (
    <div className="GridContainer" style={{ gap: "3rem" }}>
      {Object.values(items).map((item, index) => (
        <ItemDisplay item={item} key={index} />
      ))}
    </div>
  );
}

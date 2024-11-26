import { useEffect, useState } from "react";
import { getAllShopItems } from "../../Queries";
import { ShopItem } from "../../Types/ShopItem";
import { ItemDisplay } from "../SearchTab/ItemDisplay";
import { ItemLeaderboard } from "./Leaderboard";
import { ItemLeaderboardType, TimeSpan } from "../../Types/Statistics";

export function AllItemStatistics(props: { timeSpan: TimeSpan }) {
  const [items, setItems] = useState<ShopItem[]>([]);
  const { timeSpan } = props;

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
    <div className="GridContainer">
      <div className="DisplayCard">
        <h3>Topseller</h3>
        <ItemLeaderboard type={ItemLeaderboardType.TOP_SELLING_ITEMS} timeSpan={timeSpan} />
      </div>
      <div className="GridContainer" style={{ gap: "3rem" }}>
        {Object.values(items).map((item, index) => (
          <ItemDisplay item={item} key={index} />
        ))}
      </div>
    </div>
  );
}

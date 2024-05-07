import { useParams } from "react-router-dom";
import { ShopItem } from "../../Types/ShopItem";
import { useEffect, useState } from "react";
import { getAllShopItems, getOwnUser, getUser } from "../../Queries";
import { User } from "../../Types/User";
import { UserSummaryCard } from "../StatisticsTab/UserSummaryCard";
import { ErrorComponent } from "../Util/ErrorTab";
import { ItemDisplay } from "./ItemDisplay";
import { isKiosk, isUser } from "../../SessionInfo";

export function ItemSelection() {
  const { userId } = useParams();
  const [items, setItems] = useState<ShopItem[]>([]);
  const [user, setUser] = useState<User>();

  useEffect(() => {
    getAllShopItems().then((itemList) => {
      if (itemList) setItems(itemList);
    });
    if (isUser() && userId)
      if (isKiosk()) {
        getUser(userId).then((user) => {
          setUser(user);
        });
      } else {
        getOwnUser().then((user) => {
          setUser(user);
        });
      }
  }, []);

  function filter(itemList: ShopItem[]) {
    return itemList.filter((item) => item.enabled);
  }

  if (user) {
    return (
      <>
        <div style={{ flexGrow: "0" }}>
          <UserSummaryCard user={user} />
        </div>
        <div className="SmallGridContainer">
          {filter(items).map((item, index) => (
            <ItemDisplay key={index} item={item} />
          ))}
        </div>
      </>
    );
  } else {
    return <ErrorComponent />;
  }
}

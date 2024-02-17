import { useParams } from "react-router-dom";
import { ShopItem } from "../../Types/ShopItem";
import { ItemContainer } from "./ItemContainer";
import { useEffect, useState } from "react";
import { getAllShopItems, getUser } from "../Util/Queries";
import { User } from "../../Types/User";
import { UserSummaryCard } from "../StatisticsTab/UserSummaryCard";
import { ErrorComponent } from "../Route-Components/ErrorTab";

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

  if (user) {
    return (
      <>
        <UserSummaryCard user={user} />
        <ItemContainer items={items} />
      </>
    );
  } else {
    return <ErrorComponent />;
  }
}

import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import {ShopItem} from "../../Types/ShopItem.ts";
import {getAllShopItems, getOwnUser, getUser} from "../../Queries.ts";
import {isKiosk, isUser} from "../../SessionInfo.ts";
import {User} from "../../Types/User.ts";
import {UserSummaryCard} from "../StatisticsTab/UserSummaryCard.tsx";
import {ItemDisplay} from "./ItemDisplay.tsx";
import {ErrorComponent} from "../Util/ErrorTab.tsx";

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
      } else{
        getOwnUser().then((user) => {
          if(userId == user?.id){
            setUser(user);
          }
        });
      }
  }, [userId]);

  function filterItems(itemList: ShopItem[]) {
    return itemList.filter((item) => item.enabled);
  }

  // New function to group and sort items by category
  function groupAndSortItems(itemList: ShopItem[]) {
    const enabledItems = filterItems(itemList);

    // Group items by category
    const groupedItems = enabledItems.reduce((acc: Record<string, ShopItem[]>, item) => {
      if (!acc[item.category]) acc[item.category] = [];
      acc[item.category].push(item);
      return acc;
    }, {});

    // Sort categories alphabetically and each category's items by displayName
    return Object.keys(groupedItems)
        .sort((a, b) => a.localeCompare(b)) // Sort categories alphabetically by name
        .map(category => ({
          category,
          items: groupedItems[category].sort((a, b) => a.displayName.localeCompare(b.displayName))
        }));
  }

  if (user) {
    const groupedItems = groupAndSortItems(items);

    return (
        <>
          <div style={{ flexGrow: "0" }}>
            <UserSummaryCard user={user} />
          </div>
          <div id="category-box">
            {groupedItems.map(({ category, items }) => (
                <div key={category} className="ItemGroup">
                  <h2>{category}</h2>
                  <div className="SmallGridContainer">
                    {items.map((item, index) => (
                        <ItemDisplay key={index} item={item}/>
                    ))}
                  </div>
                </div>
            ))}
          </div>
        </>
    );
  } else {
    return <ErrorComponent/>;
  }
}

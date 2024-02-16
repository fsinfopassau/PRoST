import { ShopItem } from "../../Types/ShopItem";
import { ItemContainer } from "./ItemContainer";

export function ItemSelection() {
  const items: ShopItem[] = [
    {
      id: "beer",
      displayName: "Bier",
      category: "drinks",
      price: 1.50,
      enabled: true,
    },
    {
      id: "spezi",
      displayName: "Spezi",
      category: "drinks",
      price: 1,
      enabled: true,
    },
    {
      id: "wasser",
      displayName: "Wasser",
      category: "drinks",
      price: .75,
      enabled: true,
    },
  ];

  return <><h1>Shop:</h1>  
  <ItemContainer items={items}/></>;
}

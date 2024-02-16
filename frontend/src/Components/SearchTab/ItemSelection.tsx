import { useParams } from "react-router-dom";
import { ShopItem } from "../../Types/ShopItem";
import { ItemContainer } from "./ItemContainer";

export function ItemSelection() {
  const { userid } = useParams();

  const items: ShopItem[] = [
    {
      id: "beer",
      displayName: "Bier",
      category: "drinks",
      price: 1.5,
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
      price: 0.75,
      enabled: true,
    },
  ];

  console.log("asdf ", userid);

  return (
    <>
      <h1>{userid}</h1>
      <ItemContainer items={items} />
    </>
  );
}

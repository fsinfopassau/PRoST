import { ShopItem } from "../../Types/ShopItem";
import { ItemDisplay } from "./ItemDisplay";

export function ItemContainer(props: {items: ShopItem[]}){

  const items = props.items;

    return <>
    <div className="users-container">
      {items.map((item, index) => (
        <ItemDisplay key={index} item={item} />
      ))}
    </div></>

}
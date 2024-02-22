import { ShopItem } from "../../Types/ShopItem";
import { ItemDisplay } from "./ItemDisplay";

export function ItemContainer(props: { items: ShopItem[] }) {
  const items = props.items;

  function filter(itemList: ShopItem[]){
    return itemList.filter(item => item.enabled);
  }

  return (
    <>
      <div className="SelectionContainer">
        {filter(items).map((item, index) => (
          <ItemDisplay key={index} item={item} />
        ))}
      </div>
    </>
  );
}

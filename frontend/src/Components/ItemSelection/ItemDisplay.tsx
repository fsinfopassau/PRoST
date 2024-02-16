import { ShopItem } from "../../Types/ShopItem";

export function ItemDisplay(props: {item: ShopItem}){
    return <>
        <button className="Button theme">{props.item.displayName}</button>
    </>
}
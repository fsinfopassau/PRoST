import { getAllShopItems } from "../Components/Util/Queries";

export interface ShopItem {
  id: string;
  category: string;
  displayName: string;
  price: number;
  enabled: boolean;
}

let cachedItems = new Map<string, ShopItem>();

/**
 * gets DisplayName from cached Users
 */
export async function getItemDisplayName(
  itemId: string
): Promise<string | undefined> {
  let item = cachedItems.get(itemId);
  if (item) {
    return item.displayName;
  } else {
    const newItems = await getAllShopItems();
    newItems.forEach((newItem: ShopItem) =>
      cachedItems.set(newItem.id, newItem)
    );

    item = cachedItems.get(itemId);
    if (item) {
      return item.displayName;
    }
  }
  return undefined;
}

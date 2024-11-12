import {getAllShopItems} from "../Queries";

export interface ShopItem {
  id: string;
  category: string;
  displayName: string;
  price: number;
  enabled: boolean;
}

const cachedItems = new Map<string, ShopItem>();

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
    if(newItems){
      newItems.forEach((newItem: ShopItem) =>
        cachedItems.set(newItem.id, newItem)
      );

      item = cachedItems.get(itemId);
      if (item) {
        return item.displayName;
      }
    }
  }
  return undefined;
}


export function extractCategories(items: ShopItem[]): string[] {
  const categories = items.map(item => item.category);
  return Array.from(new Set(categories));
}

export function filterByCategory(items: ShopItem[], category: string): ShopItem[] {
  return items.filter(item => item.category === category);
}

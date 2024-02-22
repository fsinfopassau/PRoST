import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { ShopItem } from "../../Types/ShopItem";
import { User } from "../../Types/User";

export const apiUrl = import.meta.env.VITE_API_URL || "http://localhost:8081";

export async function getUser(userId: string) {
  const result = await (
    await fetch(`${apiUrl}/api/users/${userId}`, {
      method: "GET",
    })
  ).json();
  return result as User;
}

export async function getAllUsers() {
  const result = await (
    await fetch(`${apiUrl}/api/users`, {
      method: "GET",
    })
  ).json();
  return result as User[];
}

export async function getShopItem(itemId: string) {
  const result = await (
    await fetch(`${apiUrl}/api/shop/${itemId}`, {
      method: "GET",
    })
  ).json();
  return result as ShopItem;
}

export async function enableItem(item: ShopItem, enable: boolean): Promise<boolean> {
  const result = await (
    await fetch(`${apiUrl}/api/shop/${item.id}/${enable ? 'enable' : 'disable'}`, {
      method: "POST",
    })
  );
  return result.ok;
}

export async function getAllShopItems() {
  const result = await (
    await fetch(`${apiUrl}/api/shop`, {
      method: "GET",
    })
  ).json();
  return result as ShopItem[];
}

export async function buyItem(userId: string, itemId: string, amount: number) {
  const result = await (
    await fetch(
      `${apiUrl}/api/shop/${itemId}/consume?userId=${userId}&n=${amount}`,
      {
        method: "POST",
      }
    )
  ).json();
  return result as number;
}

export async function getHistory(amount: number) {
  const result = await (
    await fetch(`${apiUrl}/api/history?n=${amount}`, {
      method: "GET",
    })
  ).json();
  return result as ShopHistoryEntry[];
}

export async function getUserHistory(user: User, amount: number) {
  const result = await (
    await fetch(`${apiUrl}/api/history/${user.id}?n=${amount}`, {
      method: "GET",
    })
  ).json();
  return result as ShopHistoryEntry[];
}
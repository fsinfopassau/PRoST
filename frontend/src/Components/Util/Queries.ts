import axios from "axios";
import { ShopHistoryEntry } from "../../Types/ShopHistory";
import { ShopItem } from "../../Types/ShopItem";
import { User } from "../../Types/User";
import {
  getEncodedCredentials,
  resetCredentials,
  setEncodedCredentials,
} from "./SessionInfo";

export const apiUrl = import.meta.env.VITE_API_URL || "http://localhost:8081";

export async function loginNew(username: string, password: string) {
  const cred = window.btoa(`${username}:${password}`);
  return login(cred);
}

export async function login(cred: string) {
  try {
    const response = await fetch(`${apiUrl}/api/authentication`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${cred}`,
      },
    });
    if (!response.ok) {
      resetCredentials();
      return false;
    } else {
      setEncodedCredentials(cred);
      return true;
    }
  } catch (error) {
    console.error("There was an error!", error);
    return false;
  }
}

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

export async function getAllShopItems() {
  const result = await (
    await fetch(`${apiUrl}/api/shop`, {
      method: "GET",
    })
  ).json();
  return result as ShopItem[];
}

export async function buyItem(userId: string, itemId: string, amount: number) {
  const result = await fetch(
    `${apiUrl}/api/shop/consume/${itemId}?userId=${userId}&n=${amount}`,
    {
      method: "POST",
    }
  );
  return result.ok;
}

export async function createInvoice(userId: string) {
  const result = await fetch(
    `${apiUrl}/api/invoice/create/${userId}`,
    {
      method: "POST",
    }
  );
  return result.ok;
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

export async function enableItem(
  item: ShopItem,
  enable: boolean
): Promise<boolean> {
  const result = await fetch(
    `${apiUrl}/api/shop/${item.id}/${enable ? "enable" : "disable"}`,
    {
      method: "POST",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
      },
    }
  );
  return result.ok;
}

export async function deleteShopItem(item: ShopItem) {
  const result = await fetch(`${apiUrl}/api/shop/${item.id}/delete`, {
    method: "DELETE",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
    },
  });
  return result.ok;
}

export async function changeShopItem(
  item: ShopItem,
  value: string,
  path: string
) {
  const result = await fetch(
    `${apiUrl}/api/shop/${item.id}/${path}?value=${value}`,
    {
      method: "POST",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
      },
    }
  );
  return result.ok;
}

export async function uploadItemDisplayPicture(item: ShopItem, file: File) {
  const formData = new FormData();
  formData.append("file", file);

  try{
    const result = await axios.post(apiUrl + `/api/shop/${item.id}/display-picture`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
        Authorization: `Basic ${getEncodedCredentials()}`,
      },
    });
    return result.status == 200;
  }catch (e){
    return false;
  }
}

export async function getItemDisplayPicture(
  item: ShopItem
): Promise<string | null> {
  try {
    const result = await fetch(apiUrl + `/api/shop/${item.id}/picture`, {
      method: "GET",
    });

    if (result.ok && result.status === 200) {
      const blob = await result.blob();
      const url = URL.createObjectURL(blob);
      return url;
    }
  } catch (error) {
    // If there's a network error or any other error, return null
    return null;
  }
  return null;
}

import axios from "axios";
import { ShopHistoryEntry } from "./Types/ShopHistory";
import { ShopItem } from "./Types/ShopItem";
import { AuthorizedUser, User } from "./Types/User";
import { getEncodedCredentials, setAuthorizedUser } from "./SessionInfo";
import { InvoicePage } from "./Types/Invoice";

export const apiUrl = import.meta.env.VITE_API_URL || "http://localhost:8081";

export async function loginNew(
  username: string,
  password: string
): Promise<AuthorizedUser | undefined> {
  const cred = window.btoa(`${username}:${password}`);
  return login(cred);
}

export async function login(cred: string): Promise<AuthorizedUser | undefined> {
  try {
    const result = await fetch(`${apiUrl}/api/authentication`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${cred}`,
      },
    });

    if (!result.ok) {
      return undefined;
    }

    const authorizedUser = (await result.json()) as AuthorizedUser;
    authorizedUser.credentials = cred;

    setAuthorizedUser(authorizedUser);
    return authorizedUser;
  } catch (error) {
    console.log(error);
    return undefined;
  }
}

export async function getOwnUser(): Promise<User | undefined> {
  try {
    const result = await fetch(`${apiUrl}/api/users/me`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!result.ok) {
      return undefined;
    }

    return (await result.json()) as User;
  } catch (exception) {
    return undefined;
  }
}

export async function getUser(userId: string): Promise<User | undefined> {
  try {
    const result = await fetch(`${apiUrl}/api/users?id=${userId}`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!result.ok) {
      return undefined;
    }

    return ((await result.json()) as User[])[0];
  } catch (exception) {
    return undefined;
  }
}

export async function getAllUsers(): Promise<User[] | undefined> {
  try {
    const result = await fetch(`${apiUrl}/api/users`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!result.ok) {
      return undefined;
    }

    return (await result.json()) as User[];
  } catch (error) {
    return undefined;
  }
}

export async function getShopItem(
  itemId: string
): Promise<ShopItem | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/shop/item/${itemId}`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as ShopItem;
  } catch (error) {
    return undefined;
  }
}

export async function getAllShopItems(): Promise<ShopItem[] | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/shop/item`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as ShopItem[];
  } catch (error) {
    return undefined;
  }
}

export async function buyItem(
  userId: string,
  itemId: string,
  amount: number
): Promise<boolean> {
  const result = await fetch(
    `${apiUrl}/api/shop/item/${itemId}/consume?userId=${userId}&n=${amount}`,
    {
      method: "POST",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    }
  );
  return result.ok;
}

export async function getHistory(
  amount: number
): Promise<ShopHistoryEntry[] | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/history?n=${amount}`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as ShopHistoryEntry[];
  } catch (error) {
    return undefined;
  }
}

export async function getUserHistory(user: User, amount: number) {
  try {
    const response = await fetch(
      `${apiUrl}/api/history/${user.id}?n=${amount}`,
      {
        method: "GET",
        headers: {
          Authorization: `Basic ${getEncodedCredentials()}`,
          "Content-Type": "application/json",
        },
      }
    );

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as ShopHistoryEntry[];
  } catch (error) {
    return undefined;
  }
}

export async function enableItem(
  item: ShopItem,
  enable: boolean
): Promise<boolean> {
  const result = await fetch(
    `${apiUrl}/api/shop/settings/item/${item.id}/${
      enable ? "enable" : "disable"
    }`,
    {
      method: "POST",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
      },
    }
  );
  return result.ok;
}

export async function deleteShopItem(item: ShopItem): Promise<boolean> {
  const result = await fetch(
    `${apiUrl}/api/shop/settings/item/${item.id}/delete`,
    {
      method: "DELETE",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
      },
    }
  );
  return result.ok;
}

export async function changeShopItem(
  item: ShopItem,
  value: string,
  path: string
): Promise<boolean> {
  const result = await fetch(
    `${apiUrl}/api/shop/settings/item/${item.id}/${path}?value=${value}`,
    {
      method: "POST",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
      },
    }
  );
  return result.ok;
}

export async function uploadItemDisplayPicture(
  item: ShopItem,
  file: File
): Promise<boolean> {
  const formData = new FormData();
  formData.append("file", file);

  try {
    const result = await axios.post(
      apiUrl + `/api/shop/settings/item/${item.id}/picture`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Basic ${getEncodedCredentials()}`,
        },
      }
    );
    return result.status == 200;
  } catch (e) {
    return false;
  }
}

export async function getItemDisplayPicture(
  item: ShopItem
): Promise<string | undefined> {
  try {
    const result = await fetch(apiUrl + `/api/shop/item/${item.id}/picture`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (result.ok && result.status === 200) {
      const blob = await result.blob();
      const url = URL.createObjectURL(blob);
      return url;
    }
  } catch (error) {
    // If there's a network error or any other error, return null
    return undefined;
  }
  return undefined;
}

export async function getAllInvoices(): Promise<InvoicePage | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/invoices`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as InvoicePage;
  } catch (error) {
    return undefined;
  }
}

export async function createInvoices(
  userIds: string[]
): Promise<string[] | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/invoices/create`, {
      method: "POST",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userIds),
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as string[];
  } catch (error) {
    return undefined;
  }
}

export async function deleteInvoices(
  ids: number[]
): Promise<number[] | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/invoices/delete`, {
      method: "DELETE",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(ids),
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as number[];
  } catch (error) {
    return undefined;
  }
}

export async function mailInvoices(
  ids: number[]
): Promise<number[] | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/invoices/mail`, {
      method: "POST",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(ids),
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as number[];
  } catch (error) {
    return undefined;
  }
}

export async function publishInvoices(
  ids: number[]
): Promise<number[] | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/invoices/publish`, {
      method: "POST",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(ids),
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as number[];
  } catch (error) {
    return undefined;
  }
}

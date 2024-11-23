import axios from "axios";
import { ShopHistoryEntryPage } from "./Types/ShopHistory";
import { ShopItem } from "./Types/ShopItem";
import { AuthorizedUser, User } from "./Types/User";
import { getEncodedCredentials, setAuthorizedUser } from "./SessionInfo";
import { InvoicePage } from "./Types/Invoice";
import { TransactionPage } from "./Types/Transaction";

export const apiUrl = import.meta.env.VITE_API_URL || "http://localhost:8081";

export async function loginNew(username: string, password: string): Promise<AuthorizedUser | undefined> {
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
    const result = await fetch(`${apiUrl}/api/user/me`, {
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
    const result = await fetch(`${apiUrl}/api/user/info?id=${userId}`, {
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

export async function getAllUsers(includeHidden: boolean = false): Promise<User[] | undefined> {
  try {
    const result = await fetch(`${apiUrl}/api/user/info`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!result.ok) {
      return undefined;
    }

    return ((await result.json()) as User[]).filter((user: User) => !user.hidden || includeHidden);
  } catch (error) {
    return undefined;
  }
}

export async function enableUser(user: User, enable: boolean): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/user/${enable ? "enable" : "disable"}?id=${user.id}`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
    },
  });
  return result.ok;
}

export async function hideUser(user: User, hide: boolean): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/user/${hide ? "hide" : "show"}?id=${user.id}`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
    },
  });
  return result.ok;
}

export async function hideOwnUser(hide: boolean): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/user/me/${hide ? "hide" : "show"}`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
    },
  });
  return result.ok;
}

export async function createNewUser(user: User): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/user/create`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(user),
  });
  return result.ok;
}

export async function deleteUser(user: User): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/user/delete?id=${user.id}`, {
    method: "DELETE",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
    },
  });
  return result.ok;
}

export async function createTransaction(receiver: User, value: string, actionType: string): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/transaction/${actionType}?id=${receiver.id}&value=${value}`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
    },
  });
  return result.ok;
}

export async function getAllTransactions(
  size: number,
  page: number,
  receiverId: string | undefined
): Promise<TransactionPage | undefined> {
  const params = receiverId ? "&receiverId=" + receiverId : "";

  try {
    const response = await fetch(`${apiUrl}/api/transaction/list?s=${size}&p=${page}` + params, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as TransactionPage;
  } catch (error) {
    return undefined;
  }
}

export async function getPersonalTransactions(size: number, page: number): Promise<TransactionPage | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/transaction/me?s=${size}&p=${page}`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as TransactionPage;
  } catch (error) {
    return undefined;
  }
}

export async function changeUser(user: User, value: string, path: string): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/user/${path}?id=${user.id}&value=${value}`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
    },
  });
  return result.ok;
}

export async function getShopItem(itemId: string): Promise<ShopItem | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/shop/item/info?id=${itemId}`, {
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
    const response = await fetch(`${apiUrl}/api/shop/item/list`, {
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

export async function createNewShopItem(item: ShopItem): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/shop/settings/create`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(item),
  });
  return result.ok;
}

export async function buyItem(userId: string, itemId: string, amount: number): Promise<number> {
  const result = await fetch(`${apiUrl}/api/shop/item/consume?id=${itemId}&userId=${userId}&n=${amount}`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
      "Content-Type": "application/json",
    },
  });

  if ((await result.text()).indexOf("cooldown") !== -1) {
    return 1;
  }

  if (result.status == 418) {
    return 2;
  }

  if (result.ok) return 0;
  else return -1;
}

export async function getHistory(size: number, page: number): Promise<ShopHistoryEntryPage | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/history/shop/list?s=${size}&p=${page}`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as ShopHistoryEntryPage;
  } catch (error) {
    return undefined;
  }
}

export async function getUserHistory(userId: string, size: number, page: number) {
  try {
    const response = await fetch(`${apiUrl}/api/history/shop/list?s=${size}&p=${page}&receiverId=${userId}`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as ShopHistoryEntryPage;
  } catch (error) {
    return undefined;
  }
}

export async function getOwnHistory(size: number, page: number) {
  try {
    const response = await fetch(`${apiUrl}/api/history/shop/me?s=${size}&p=${page}`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      return undefined;
    }

    return (await response.json()) as ShopHistoryEntryPage;
  } catch (error) {
    return undefined;
  }
}

export async function enableItem(item: ShopItem, enable: boolean): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/shop/settings/item/${enable ? "enable" : "disable"}?id=${item.id}`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
    },
  });
  return result.ok;
}

export async function deleteShopItem(item: ShopItem): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/shop/settings/item/delete?id=${item.id}`, {
    method: "DELETE",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
    },
  });
  return result.ok;
}

export async function changeShopItem(item: ShopItem, value: string, path: string): Promise<boolean> {
  const result = await fetch(`${apiUrl}/api/shop/settings/item/${path}?id=${item.id}&value=${value}`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${getEncodedCredentials()}`,
    },
  });
  return result.ok;
}

export async function uploadItemDisplayPicture(item: ShopItem, file: File): Promise<boolean> {
  const formData = new FormData();
  formData.append("file", file);

  try {
    const result = await axios.post(apiUrl + `/api/shop/settings/item/picture?id=${item.id}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
        Authorization: `Basic ${getEncodedCredentials()}`,
      },
    });
    return result.status == 200;
  } catch (e) {
    return false;
  }
}

export async function getItemDisplayPicture(item: ShopItem): Promise<string | undefined> {
  try {
    const result = await fetch(apiUrl + `/api/shop/item/picture?id=${item.id}`, {
      method: "GET",
      headers: {
        Authorization: `Basic ${getEncodedCredentials()}`,
        "Content-Type": "application/json",
      },
    });

    if (result.ok && result.status === 200) {
      const blob = await result.blob();
      return URL.createObjectURL(blob);
    }
  } catch (error) {
    // If there's a network error or any other error, return null
    return undefined;
  }
  return undefined;
}

export async function getPersonalInvoices(): Promise<InvoicePage | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/invoice/me`, {
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

export async function getAllInvoices(
  page: number,
  userId: string | undefined,
  mailed: boolean | undefined
): Promise<InvoicePage | undefined> {
  const params =
    (userId ? "&userId=" + userId : "") + (mailed === undefined ? "" : "&mailed=" + (mailed ? "true" : "false"));

  try {
    const response = await fetch(`${apiUrl}/api/invoice/list?s=20&p=` + page + params, {
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

export async function createInvoices(userIds: string[]): Promise<string[] | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/invoice/create`, {
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

export async function deleteInvoices(ids: number[]): Promise<number[] | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/invoice/delete`, {
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

export async function mailInvoices(ids: number[]): Promise<number[] | undefined> {
  try {
    const response = await fetch(`${apiUrl}/api/invoice/mail`, {
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

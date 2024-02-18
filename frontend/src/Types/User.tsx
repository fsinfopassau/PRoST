import { getAllUsers } from "../Components/Util/Queries";
import { UserRole } from "./UserRole";

export interface User {
  id: string; // id
  displayName: string;
  balance: number;
  enabled: boolean;
  role: UserRole;
}

export function formatMoney(balance: number, decimalCount = 2): string {
  const formatted = new Intl.NumberFormat("de-DE", {
    minimumFractionDigits: decimalCount,
    maximumFractionDigits: decimalCount,
  }).format(balance);

  return formatted + " €";
}

let cachedUsers = new Map<string, User>();

/**
 * gets DisplayName from cached Users
 */
export async function getUserDisplayName(
  userId: string
): Promise<string | undefined> {
  let user = cachedUsers.get(userId);
  if (user) {
    return user.displayName;
  } else {
    const newUsers = await getAllUsers();
    newUsers.forEach((newUser: User) => cachedUsers.set(newUser.id, newUser));

    user = cachedUsers.get(userId);
    if (user) {
      return user.displayName;
    }
  }
  return undefined;
}

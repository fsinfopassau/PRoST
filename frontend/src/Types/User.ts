import { getAllUsers } from "../Queries";
export interface User {
  id: string; // id
  displayName: string;
  balance: number;
  enabled: boolean;
}

export enum UserRole {
  UNASSIGNED,
  FSINFO,
  KIOSK,
  KAFFEEKASSE,
}

// Session-Data
export interface AuthorizedUser {
  email: string;
  displayName: string;
  accessRole: UserRole;
  credentials: string; // Basic-Auth (base 64)
}

const cachedUsers = new Map<string, User>();

/**
 * gets DisplayName from cached Users
 */
export async function getUserDisplayName(userId: string): Promise<string> {
  let user = cachedUsers.get(userId);

  if (user) {
    return user.displayName;
  } else {
    const newUsers = await getAllUsers();
    if (newUsers)
      newUsers.forEach((newUser: User) => cachedUsers.set(newUser.id, newUser));

    user = cachedUsers.get(userId);
    if (user) {
      return user.displayName;
    }
  }
  return userId;
}

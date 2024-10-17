import { AuthorizedUser } from "./Types/User";

const firstUser = localStorage.getItem("user");
let authorizedUser = firstUser !== null ? (JSON.parse(firstUser) as AuthorizedUser) : undefined;

export function getEncodedCredentials(): string {
  return authorizedUser === undefined ? "" : authorizedUser.credentials;
}

export function getSessionUserName(): string | undefined {
  if (authorizedUser === undefined) {
    return undefined;
  }

  return window.atob(authorizedUser.credentials).split(":")[0];
}

export function resetSession() {
  authorizedUser = undefined;
  localStorage.removeItem("user");
}

export function existCredentials(): boolean {
  return authorizedUser !== undefined;
}

export function setAuthorizedUser(user: AuthorizedUser) {
  authorizedUser = user;
  localStorage.setItem("user", JSON.stringify(user));
  console.log("Current User: " + JSON.stringify(user));
}

export function getAuthorizedUser(): AuthorizedUser | undefined {
  return authorizedUser;
}

// JS-Enum-Comparison is Shit! (normal comp of two enums always returned false)

export function isAdmin(): boolean {
  if (authorizedUser === undefined) return false;
  return authorizedUser.accessRole.toString() === "KAFFEEKASSE";
}

export function isKiosk(): boolean {
  if (authorizedUser === undefined) return false;
  return isAdmin() || authorizedUser.accessRole.toString() === "KIOSK";
}

export function isOnlyKiosk(): boolean {
  if (authorizedUser === undefined) return false;
  return !isAdmin() && authorizedUser.accessRole.toString() === "KIOSK";
}

export function isUser(): boolean {
  if (authorizedUser === undefined) return false;
  return isKiosk() || authorizedUser.accessRole.toString() === "FSINFO";
}

export function isOnlyUser(): boolean {
  if (authorizedUser === undefined) return false;
  return !isAdmin() && !isKiosk() && authorizedUser.accessRole.toString() === "FSINFO";
}

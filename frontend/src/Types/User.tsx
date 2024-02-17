import { UserRole } from "./UserRole";

export interface User {
  username: string; // id
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

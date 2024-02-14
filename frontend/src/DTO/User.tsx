import { UserRole } from "./UserRole";

export interface User {
  name: string; // id
  balance: number;
  enabled: boolean;
  role: UserRole;
}

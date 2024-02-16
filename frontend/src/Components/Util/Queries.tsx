import { User } from "../../Types/User";

export const apiUrl = import.meta.env.VITE_API_URL as string;

export async function getUser(userId: string) {
  const result = await (
    await fetch(`${apiUrl}/api/users/${userId}`, {
      method: "GET",
    })
  ).json();
  return result as User;
}

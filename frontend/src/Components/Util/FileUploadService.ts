// FileUploadService.ts
import axios from "axios";
import { ShopItem } from "../../Types/ShopItem";

export const apiUrl = import.meta.env.VITE_API_URL || "http://localhost:8081";

const uploadFile = (path: string, file: File) => {
  const formData = new FormData();
  formData.append("file", file);

  return axios.post(apiUrl + path, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

export async function uploadItemDisplayPicture(item: ShopItem, file: File) {
  uploadFile(`/api/shop/${item.id}/display-picture`, file);
}

export async function getItemDisplayPicture(
  item: ShopItem
): Promise<string | null> {
  try {
    const result = await fetch(
      apiUrl + `/api/shop/${item.id}/display-picture`,
      {
        method: "GET",
      }
    );

    if (!result.ok) {
      return null;
    }

    // Check if the request was successful
    if (!result.ok) {
      // If the request was not successful, return null
      return null;
    }

    const blob = await result.blob();
    const url = URL.createObjectURL(blob);
    return url;
  } catch (error) {
    // If there's a network error or any other error, return null
    return null;
  }
}

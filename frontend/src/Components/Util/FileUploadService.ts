// FileUploadService.ts
import axios from 'axios';
import { ShopItem } from '../../Types/ShopItem';

export const apiUrl = import.meta.env.VITE_API_URL || "http://localhost:8081";

const uploadFile = (path: string, file: File) => {
  const formData = new FormData();
  formData.append('file', file);

  return axios.post(apiUrl+path, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export function uploadItemDisplayPicture(item: ShopItem, file: File){
    uploadFile(`/api/shop/${item.id}/display-picture`,file);
}
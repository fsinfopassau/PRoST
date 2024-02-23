package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  public static final String ITEM_LOCATION = "/tmp/kdv/items/";

  public boolean saveItemPicture(ShopItem item, MultipartFile file) throws IOException {
    if (file.getContentType().endsWith("png")) {
      String path = getItemPicturePath(item);
      saveFile(file, path);
      System.out.println(
          "[FileStorageService] :: Save file to " + path);
      return true;
    }
    return false;
  }

  public void saveFile(MultipartFile multipartFile, String destinationPath) throws IOException {
    File file = new File(destinationPath);
    multipartFile.transferTo(file);
  }

  public File getItemPicture(ShopItem item) throws MalformedURLException {
    String filePath = getItemPicturePath(item);
    System.out.println("[FileStorageService] :: Get file from " + filePath);
    return new File(filePath);
  }

  private String getItemPicturePath(ShopItem item) {
    return URI.create(ITEM_LOCATION).resolve(item.getId() + ".png").normalize().toString();
  }

}

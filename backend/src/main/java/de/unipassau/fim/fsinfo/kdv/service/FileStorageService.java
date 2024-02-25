package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  private final String itemLocation;

  @Autowired
  public FileStorageService(@Value("${kdv.save-location}") String saveLocation) {
    itemLocation = saveLocation + "/items";
  }

  public FileStorageService(String customSave, boolean printLocation) throws IOException {
    itemLocation = customSave + "/items";
    Files.createDirectories(new File(itemLocation).toPath());
    if (printLocation) {
      System.out.println("item-location: " + itemLocation);
    }
  }

  public boolean saveItemPicture(ShopItem item, MultipartFile file) throws IOException {
    if (item != null && file != null && file.getContentType() != null && file.getContentType()
        .endsWith("png")) {
      String path = getItemPicturePath(item);
      saveFile(file, path);
      return true;
    }
    return false;
  }

  private void saveFile(MultipartFile multipartFile, String destinationPath) throws IOException {
    File file = new File(destinationPath);
    file.getParentFile().mkdirs();
    multipartFile.transferTo(file);
    // System.out.println("[FileStorageService] :: Save file to " + destinationPath);
  }

  public Optional<File> getItemPicture(ShopItem item) {
    String filePath = getItemPicturePath(item);
    // System.out.println("[FileStorageService] :: Get file from " + filePath);

    File f = new File(filePath);
    if (f.exists() && f.isFile()) {
      return Optional.of(f);
    }
    return Optional.empty();
  }

  private String getItemPicturePath(ShopItem item) {
    return itemLocation + "/" + item.getId() + ".png";
  }

}

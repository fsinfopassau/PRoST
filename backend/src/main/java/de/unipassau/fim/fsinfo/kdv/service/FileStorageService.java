package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  private final String itemLocation;

  @Autowired
  public FileStorageService(@Value("${kdv.save-location}") @NonNull String saveLocation) {
    itemLocation = saveLocation + "/items";
    System.out.println("[FSS] :: item-location: " + itemLocation);
  }

  public FileStorageService(@NonNull String customSave, boolean printLocation) throws IOException {
    itemLocation = customSave + "/items";
    Files.createDirectories(new File(itemLocation).toPath());
    if (printLocation) {
      System.out.println("item-location: " + itemLocation);
    }
  }

  public boolean saveItemPicture(@NonNull ShopItem item, @NonNull MultipartFile file)
      throws IOException {
    if (file.getContentType() != null && file.getContentType()
        .endsWith("png")) {
      String path = getItemPicturePath(item);
      saveFile(file, path);
      return true;
    }
    return false;
  }

  private void saveFile(@NonNull MultipartFile multipartFile, @NonNull String destinationPath)
      throws IOException {
    File file = new File(destinationPath);
    file.getParentFile().mkdirs();
    multipartFile.transferTo(file);
  }

  public Optional<File> getItemPicture(@NonNull ShopItem item) {
    String filePath = getItemPicturePath(item);

    File f = new File(filePath);
    if (f.exists() && f.isFile()) {
      return Optional.of(f);
    }
    return Optional.empty();
  }

  private String getItemPicturePath(@NonNull ShopItem item) {
    return itemLocation + "/" + item.getId() + ".png";
  }

}

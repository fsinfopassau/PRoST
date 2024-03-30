package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  private static final String[] PICTURE_TYPES = {"image/jpeg", "image/png", "image/gif"};

  private static final String sep = System.getProperty("file.separator");

  private final File itemLocation;

  @Autowired
  public FileStorageService(@Value("${kdv.save-location}") @NonNull String saveLocation)
      throws IOException {
    itemLocation = new File(saveLocation + sep + "items").getAbsoluteFile();
    Files.createDirectories(itemLocation.toPath());
    System.out.println("[FSS] :: item-location: " + itemLocation);
  }

  public FileStorageService(@NonNull String customSave, boolean printLocation) throws IOException {
    itemLocation = new File(customSave + sep + "items").getAbsoluteFile();
    Files.createDirectories(itemLocation.toPath());
    if (printLocation) {
      System.out.println("item-location: " + itemLocation);
    }
  }

  @Transactional
  public boolean saveItemPicture(@NonNull ShopItem item, @NonNull MultipartFile file)
      throws IOException {
    if (file.getContentType() != null && isValidImageType(file)) {
      String path = getItemPicturePath(item);
      saveFile(file, path);
      return true;
    }
    System.out.println("[FSS] :: wrong contentType \"" + file.getContentType() + "\"");
    return false;
  }

  private void saveFile(@NonNull MultipartFile multipartFile, @NonNull String destinationPath)
      throws IOException {
    File file = new File(destinationPath);
    //noinspection ResultOfMethodCallIgnored
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
    return itemLocation.getAbsolutePath() + sep + item.getId();
  }

  private boolean isValidImageType(MultipartFile file) {
    return Arrays.stream(PICTURE_TYPES)
        .anyMatch(s -> Objects.equals(file.getContentType(), s));
  }

}

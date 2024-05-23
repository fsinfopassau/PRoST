package de.unipassau.fim.fsinfo.prost.service;

import static org.mockito.Mockito.when;

import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileStorageServiceTest {

  private FileStorageService service;
  private Path path;

  @BeforeEach
  public void setUp(@TempDir Path path) throws IOException {
    MockitoAnnotations.openMocks(this);
    this.path = path;
    service = new FileStorageService(path.toString(), true);
  }

  @Test
  void saveItemPictureNonPng() throws IOException {
    ShopItem item = new ShopItem("test", "testCategory", "Test Item", new BigDecimal("420.69"));
    MultipartFile mockFile = Mockito.mock(MultipartFile.class);
    when(mockFile.getContentType()).thenReturn("hallo Welt");

    File expectedFile = new File(path + "/items/test");

    Assertions.assertFalse(service.saveItemPicture(item, mockFile));
    Assertions.assertFalse(expectedFile.exists());
  }

  @Test
  void saveItemPicture() throws IOException {
    ShopItem item = new ShopItem("test", "testCategory", "Test Item", new BigDecimal("420.69"));
    MockMultipartFile multipartFile = new MockMultipartFile(
        "do-not-show", // request parameter name
        "unimportant.png", // original file name
        "image/png", // content type
        new byte[0] // file content
    );
    File expectedFile = new File(path + "/items/test");

    Assertions.assertTrue(service.saveItemPicture(item, multipartFile));
    Assertions.assertTrue(expectedFile.exists());
  }

  @Test
  void getItemPicture() throws IOException {
    ShopItem item = new ShopItem("test", "testCategory", "Test Item", new BigDecimal("420.69"));
    MockMultipartFile multipartFile = new MockMultipartFile(
        "do-not-show", // request parameter name
        "unimportant.png", // original file name
        "image/png", // content type
        new byte[0] // file content
    );
    File expectedFile = new File(path + "/items/test");

    Assertions.assertTrue(service.saveItemPicture(item, multipartFile));

    Assertions.assertTrue(service.getItemPicture(item).isPresent());
    Assertions.assertEquals(service.getItemPicture(item).get().getAbsolutePath(),
        expectedFile.getAbsolutePath());
  }

  @Test
  void getItemPictureMissing() throws IOException {
    ShopItem item = new ShopItem("test", "testCategory", "Test Item", new BigDecimal("420.69"));

    Optional<File> picture = service.getItemPicture(item);

    Assertions.assertFalse(picture.isPresent());
  }
}

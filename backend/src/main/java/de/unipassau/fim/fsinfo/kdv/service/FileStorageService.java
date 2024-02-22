package de.unipassau.fim.fsinfo.kdv.service;

import java.io.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  public static final String SAVE_LOCATION = "/tmp/kdv/img";

  public void saveItemPicture(String itemId, MultipartFile file) {
    System.out.println("[FileStorageService] :: Save file to "+SAVE_LOCATION+"/items/"+itemId);
  }

  public MultipartFile getItemPicture(String itemId){
    System.out.println("[FileStorageService] :: Get file from "+SAVE_LOCATION+"/items/"+itemId);
    return null;
  }

}

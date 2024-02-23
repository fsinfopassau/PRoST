package de.unipassau.fim.fsinfo.kdv.service;

import java.io.File;
import jdk.jfr.ContentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  public static final String SAVE_LOCATION = "/tmp/kdv/img";
  public static final String allowedFiletypes[] = {"png"};

  public void saveItemPicture(String itemId, MultipartFile file) {
    if(file.getContentType().endsWith("png")){

    }
    System.out.println(file.getContentType());
    System.out.println("[FileStorageService] :: Save file to "+SAVE_LOCATION+"/items/"+itemId);
  }

  public MultipartFile getItemPicture(String itemId){
    System.out.println("[FileStorageService] :: Get file from "+SAVE_LOCATION+"/items/"+itemId);
    return null;
  }

}

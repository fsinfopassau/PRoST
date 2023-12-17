package de.unipassau.fim.fsinfo.kdv.service;

import org.springframework.stereotype.Service;

@Service
public class UserAuthService {

  public boolean auth(String apiKey) {
    // TODO
    return apiKey.equals("asdf");
  }

}



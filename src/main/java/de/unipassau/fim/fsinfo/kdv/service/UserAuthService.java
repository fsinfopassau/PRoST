package de.unipassau.fim.fsinfo.kdv.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

@Configuration
@ApplicationScope
public class UserAuthService {

  public boolean auth(String apiKey) {
    // TODO
    return apiKey.equals("asdf");
  }

}



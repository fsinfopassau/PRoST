package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository users;

  private static final String EMAIL_PATTERN = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";

  @Autowired
  public UserService(UserRepository users) {
    this.users = users;
  }

  public static boolean isValidEmail(String email) {
    if (email == null) {
      return false;
    }

    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  public Optional<KdvUser> createUser(String userName, String displayName, String email) {

    if (users.findById(userName).isPresent()) {
      System.out.println("[US] :: " + userName + " :: user-creation failed :: exists ");
      return Optional.empty();
    }

    // Allow only valid or no mail.
    if (!isValidEmail(email) && email != null) {
      System.out.println("[US] :: " + userName + " :: user-creation failed [mail=" + email + "] ");
      return Optional.empty();
    }

    if (displayName == null || displayName.isBlank()) {
      System.out.println(
          "[US] :: " + userName + " :: user-creation failed [displayName=" + displayName + "] ");
      return Optional.empty();
    }

    KdvUser user = new KdvUser(userName, displayName, email, true);
    System.out.println("[US] :: " + userName + " :: user-creation succeeded");
    users.save(user);
    return Optional.of(user);
  }
}

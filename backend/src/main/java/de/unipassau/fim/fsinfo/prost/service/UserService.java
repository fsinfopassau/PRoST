package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional
  public Optional<ProstUser> createUser(String userName, String displayName, String email) {

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

    ProstUser user = new ProstUser(userName, displayName, email, true);
    System.out.println("[US] :: " + userName + " :: user-creation succeeded");
    users.save(user);
    return Optional.of(user);
  }

  public Optional<List<ProstUser>> info(String id) {
    if (id == null) {
      return Optional.of(users.findAll());
    } else {
      Optional<ProstUser> user = users.findById(id);
      if (user.isPresent()) {
        return Optional.of(List.of(user.get()));
      }
    }
    return Optional.empty();
  }

  @Transactional
  public boolean delete(String id) {
    Optional<ProstUser> user = users.findById(id);

    if (user.isPresent()) {
      users.delete(user.get());
      return true;
    }
    return false;
  }

  @Transactional
  public boolean rename(String id, String name) {
    Optional<ProstUser> user = users.findById(id);

    if (user.isPresent()) {
      ProstUser u = user.get();
      u.setDisplayName(name);
      users.save(u);
      return true;
    }
    return false;
  }

  @Transactional
  public boolean setEmail(String id, String email) {
    Optional<ProstUser> user = users.findById(id);

    if (user.isPresent() && isValidEmail(email)) {
      ProstUser u = user.get();
      u.setEmail(email);
      users.save(u);
      return true;
    }
    return false;
  }

  @Transactional
  public boolean setEnabled(String id, boolean value) {
    Optional<ProstUser> user = users.findById(id);

    if (user.isPresent()) {
      ProstUser u = user.get();
      u.setEnabled(value);
      users.save(u);
      return true;
    }
    return false;
  }

  @Transactional
  public boolean setMoneySpent(String id, String amountString) {
    Optional<ProstUser> user = users.findById(id);
    try {
      BigDecimal amount = new BigDecimal(amountString);

      if (user.isPresent()) {
        ProstUser u = user.get();
        u.setTotalSpent(amount);
        users.save(u);
        return true;
      }
      return false;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}

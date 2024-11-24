package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.DataFilter;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository users;

  @Autowired
  public UserService(UserRepository users) {
    this.users = users;
  }

  @Transactional
  public Optional<ProstUser> createUser(String userName, String displayName, String email,
      boolean manualCreation) {

    if (users.findById(userName).isPresent()) {
      System.out.println("[US] :: " + userName + " :: user-creation failed :: exists ");
      return Optional.empty();
    }

    if (!DataFilter.isValidString(userName, "userName")) {
      System.out.println(
          "[US] :: " + userName + " :: user-creation failed [userName=" + userName + "] ");
      return Optional.empty();
    }

    if (manualCreation) {
      userName = DataFilter.filterNameId(userName);
    }

    // Allow only valid or no mail at all.
    if (!DataFilter.isValidEmail(email) && email != null) {
      System.out.println("[US] :: " + userName + " :: user-creation failed [mail=" + email + "] ");
      return Optional.empty();
    }

    if (!DataFilter.isValidString(displayName, "displayName")) {
      System.out.println(
          "[US] :: " + userName + " :: user-creation failed [displayName=" + displayName + "] ");

      // refuse creation if user was created by admin
      if (manualCreation) {
        return Optional.empty();
      }
      // default to id
      displayName = userName;
    }

    ProstUser user = new ProstUser(userName, displayName, email, true, false);
    users.save(user);
    System.out.println("[US] :: " + userName + " :: user-creation succeeded");
    return Optional.of(user);
  }

  public Optional<List<ProstUser>> info(String id, boolean showHidden) {
    if (id == null) {
      return Optional.of(users.findAll().stream()
          .filter(user -> showHidden || Boolean.FALSE.equals(user.getHidden()))
          .toList());
    } else {
      Optional<ProstUser> user = users.findById(id);
      if (user.isPresent() && (showHidden || Boolean.FALSE.equals(user.get().getHidden()))) {
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

    if (user.isPresent() && DataFilter.isValidString(name, "user name")) {
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

    if (user.isPresent() && DataFilter.isValidString(email, "user email")
        && DataFilter.isValidEmail(email)) {
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
  public boolean setHidden(String id, boolean value) {
    Optional<ProstUser> user = users.findById(id);

    if (user.isPresent()) {
      ProstUser u = user.get();
      u.setHidden(value);
      users.save(u);
      return true;
    }
    return false;
  }

  @Transactional
  public boolean setKiosk(String id, boolean value) {
    Optional<ProstUser> user = users.findById(id);

    if (user.isPresent()) {
      ProstUser u = user.get();
      u.setKiosk(value);
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

      if (!DataFilter.isValidMoney(amount)) {
        System.out.println("[US] :: Price-value with " + amount + " not valid!");
        return false;
      }

      if (amount.compareTo(BigDecimal.ZERO) < 0) { // Only Positive Values
        System.out.println("[US] :: Money Spent is with " + amount + " too low");
        return false;
      }

      if (user.isPresent()) {
        ProstUser u = user.get();
        u.setTotalSpent(amount.abs());
        users.save(u);
        return true;
      } else {
        System.out.println("[US] :: No user with id " + id + " found");
        return false;
      }
    } catch (NumberFormatException e) {
      System.out.println("[US] :: " + amountString + " is no valid amount");
      return false;
    }
  }
}

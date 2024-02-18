package de.unipassau.fim.fsinfo.kdv.data.dao;

import de.unipassau.fim.fsinfo.kdv.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity(name = "KDV_User")
public class User {

  @Id
  @Column(nullable = false, unique = true)
  private String id;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserRole role;
  @Column(nullable = false)
  private Double balance;
  private String displayName;
  private Boolean enabled;

  public User(String id, UserRole role,
      Boolean enabled) {
    this(id, null,role,enabled);
  }

  public User(String id, String displayName, UserRole role, Boolean enabled){
    this.role = role;
    this.id = id;
    this.displayName = displayName;
    this.enabled = enabled;
    this.balance = 0D;
  }

  @Deprecated
  public User() {
  }

  public String getId() {
    return id;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public void setId(String username) {
    this.id = username;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole userRole) {
    this.role = userRole;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String toString() {
    return "User{" +
        ", id='" + id + '\'' +
        ", displayName=" + displayName +
        ", role=" + role +
        ", balance=" + balance +
        ", enabled=" + enabled +
        '}';
  }
}

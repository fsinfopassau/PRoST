package de.unipassau.fim.fsinfo.kdv.data.dao;

import de.unipassau.fim.fsinfo.kdv.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "KDV_User")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Column(nullable = false, unique = true)
  private String name;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserRole role;
  @Column(nullable = false)
  private Double balance;
  private Boolean enabled;

  public User(String name, UserRole role,
      Boolean enabled) {
    this.role = role;
    this.name = name;
    this.enabled = enabled;
    this.balance = 0D;
  }

  @Deprecated
  public User() {
  }

  public String getUsername() {
    return name;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Long getId() {
    return id;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public void setName(String username) {
    this.name = username;
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

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", role=" + role +
        ", balance=" + balance +
        ", enabled=" + enabled +
        '}';
  }
}

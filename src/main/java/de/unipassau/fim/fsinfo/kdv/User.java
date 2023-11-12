package de.unipassau.fim.fsinfo.kdv;

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
  private String password;
  private double balance;
  private Boolean enabled;

  public User(String name, UserRole role,
      Boolean enabled) {
    this.role = role;
    this.name = name;
    this.password = password;
    this.enabled = enabled;
  }

  public User() {
  }

  public String getPassword() {
    return password;
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

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public void setName(String username) {
    this.name = username;
  }

  public void setPassword(String password) {
    this.password = password;
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
}

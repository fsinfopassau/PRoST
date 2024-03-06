package de.unipassau.fim.fsinfo.kdv.data.dao;

import de.unipassau.fim.fsinfo.kdv.data.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "KDV_User")
public class KdvUser {

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

  public KdvUser(String id, String displayName, UserRole role, Boolean enabled) {
    this.role = role;
    this.id = id;
    this.displayName = displayName;
    this.enabled = enabled;
    this.balance = 0D;
  }
}


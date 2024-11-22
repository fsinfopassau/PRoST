package de.unipassau.fim.fsinfo.prost.data.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "PRoST_User")
public class ProstUser {

  @Id
  @Column(nullable = false, unique = true)
  private String id;
  @Column(nullable = false)
  private BigDecimal balance;
  @Column(nullable = false)
  private BigDecimal totalSpent;
  @Column(nullable = false)
  private String displayName;
  private String email;
  private Boolean enabled;
  private Boolean hidden;

  public ProstUser(String id, String displayName, String email, Boolean enabled) {
    this.id = id;
    this.displayName = displayName;
    this.email = email;
    this.enabled = enabled;
    this.balance = new BigDecimal(0);
    this.totalSpent = new BigDecimal(0);
  }

  public ProstUser(String id, String displayName, String email, Boolean enabled, Boolean hidden) {
    this.id = id;
    this.displayName = displayName;
    this.email = email;
    this.enabled = enabled;
    this.hidden = hidden;
    this.balance = new BigDecimal(0);
    this.totalSpent = new BigDecimal(0);
  }
}


package de.unipassau.fim.fsinfo.prost.data.dao;


import de.unipassau.fim.fsinfo.prost.data.type.AdminHistoryDomain;
import de.unipassau.fim.fsinfo.prost.data.type.AdminHistoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "PRoST_AdminHistoryEntry")
public class AdminHistoryEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Column(nullable = false)
  private Long timestamp;

  @Column(nullable = false)
  private AdminHistoryDomain domain;
  @Column(nullable = false)
  private AdminHistoryType type;

  @Column(nullable = false)
  private String changer;

  private String previousValue;
  private String newValue;


  public AdminHistoryEntry(AdminHistoryType type, AdminHistoryDomain domain) {
    this.type = type;
    this.timestamp = Instant.now().toEpochMilli();
    this.domain = domain;
  }

}

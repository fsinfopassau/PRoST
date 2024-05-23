package de.unipassau.fim.fsinfo.prost.data.dao;

import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity(name = "PRoST_TransactionHistoryEntry")
public class TransactionEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String bearerId;

  private String senderId;

  @Column(nullable = false)
  private String receiverId;

  @Column(nullable = false)
  private TransactionType transactionType;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private BigDecimal previous;

  @Column(nullable = false)
  private Long timestamp;

  public TransactionEntry(String senderId, String receiverId, String bearerId,
      TransactionType type,
      BigDecimal previous, BigDecimal amount) {
    this.bearerId = bearerId;
    this.receiverId = receiverId;
    this.senderId = senderId;
    this.transactionType = type;
    this.previous = previous;
    this.amount = amount;
    this.timestamp = Instant.now().toEpochMilli();
  }
}

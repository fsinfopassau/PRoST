package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.data.repositories.TransactionRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

  private final TransactionRepository history;
  private final UserRepository users;

  @Autowired
  public TransactionService(UserRepository users, TransactionRepository history) {
    this.history = history;
    this.users = users;
  }

  @Transactional
  public Optional<TransactionEntry> buy(String userId, BigDecimal amount, String bearerId) {
    Optional<ProstUser> user = users.findById(userId);
    Optional<ProstUser> bearer = users.findById(bearerId);

    if (user.isPresent()) {
      return moneyTransaction(Optional.empty(), user, bearer, amount, TransactionType.BUY);
    }
    return Optional.empty();
  }

  @Transactional
  public Optional<TransactionEntry> deposit(String userId, String amountString,
      String bearerId) {
    Optional<ProstUser> user = users.findById(userId);
    Optional<ProstUser> bearer = users.findById(bearerId);

    BigDecimal amount;
    try {
      amount = new BigDecimal(amountString);
    } catch (NumberFormatException e) {
      return Optional.empty();
    }

    if (user.isPresent()) {
      return moneyTransaction(Optional.empty(), user, bearer, amount, TransactionType.DEPOSIT);
    }
    return Optional.empty();
  }

  private Optional<TransactionEntry> moneyTransaction(Optional<ProstUser> sender,
      Optional<ProstUser> receiver,
      Optional<ProstUser> bearer, BigDecimal amount, TransactionType type) {

    if (receiver.isEmpty() || bearer.isEmpty() || amount.intValue() <= 0) {
      System.err.println(receiver + " " + bearer + " " + amount);
      return Optional.empty();
    }

    switch (type) {
      case DEPOSIT -> {
        ProstUser u = receiver.get();
        u.setBalance(u.getBalance().add(amount.abs()));
        users.save(u);

        TransactionEntry entry = new TransactionEntry(null,
            receiver.get().getId(),
            bearer.get().getId(), type, amount);
        history.save(entry);
        return Optional.of(entry);
      }
      default -> {
        return Optional.empty();
      }
    }
  }

}

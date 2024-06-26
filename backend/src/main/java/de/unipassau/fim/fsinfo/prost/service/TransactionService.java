package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.DataFilter;
import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.data.repositories.TransactionRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

  private final TransactionRepository history;
  private final UserRepository users;

  public static final BigDecimal MAX_DEPOSIT_VALUE = new BigDecimal("50.0");

  @Autowired
  public TransactionService(UserRepository users, TransactionRepository history) {
    this.history = history;
    this.users = users;
  }

  @Transactional
  public Optional<TransactionEntry> moneyTransfer(Optional<String> senderId, String receiverId,
      String bearerId, BigDecimal amount, TransactionType type) {
    Optional<ProstUser> receiver = users.findById(receiverId);
    Optional<ProstUser> bearer = users.findById(bearerId);
    Optional<ProstUser> sender = senderId.flatMap(users::findById);

    return moneyTransfer(sender, receiver, bearer, amount, type);
  }

  @Transactional
  public Optional<TransactionEntry> moneyTransfer(Optional<ProstUser> sender,
      Optional<ProstUser> receiver,
      Optional<ProstUser> bearer, BigDecimal amount, TransactionType type) {

    if (!DataFilter.isValidMoney(amount)) {
      System.err.println("[TS] :: " + amount + " has not the right money-precision!");
      return Optional.empty();
    }

    // scale -> Maximum 2 Decimal -> smallest is Cents, no fraction of cents
    if (receiver.isEmpty() || bearer.isEmpty()) {
      System.err.println("[TS] :: something is missing :: " + receiver + " " + bearer);
      return Optional.empty();
    }

    switch (type) {
      case DEPOSIT -> {
        return deposit(receiver.get(), bearer.get(), amount);
      }
      case BUY -> {
        return buy(receiver.get(), bearer.get(), amount);
      }
      case CHANGE -> {
        return change(receiver.get(), bearer.get(), amount);
      }
      default -> {
        System.out.println("[TS] :: Transaction-type \"" + type + "\" is not defined");
        return Optional.empty();
      }
    }
  }


  private Optional<TransactionEntry> deposit(ProstUser receiver, ProstUser bearer,
      BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      System.out.println("[TS] :: Only deposit positive values!");
      return Optional.empty();
    }
    if (amount.compareTo(MAX_DEPOSIT_VALUE) > 0) {
      System.out.println("[TS] :: Max-Deposit amount of " + MAX_DEPOSIT_VALUE.doubleValue() + "!");
      return Optional.empty();
    }

    BigDecimal previous = receiver.getBalance();

    receiver.setBalance(previous.add(amount.abs()));
    users.save(receiver);

    TransactionEntry entry = new TransactionEntry(null,
        receiver.getId(),
        bearer.getId(), TransactionType.DEPOSIT, previous, amount.abs());
    history.save(entry);
    return Optional.of(entry);
  }

  private Optional<TransactionEntry> buy(ProstUser receiver, ProstUser bearer,
      BigDecimal amount) {

    if (amount.compareTo(BigDecimal.ZERO) < 0) { // Only Positive Values
      System.out.println("[TS] :: Buy is with " + amount + " too low");
      return Optional.empty();
    }

    BigDecimal previous = receiver.getBalance();

    receiver.setBalance(previous.subtract(amount.abs()));
    receiver.setTotalSpent(receiver.getTotalSpent().abs().add(amount.abs()));
    users.save(receiver);

    TransactionEntry entry = new TransactionEntry(null,
        receiver.getId(),
        bearer.getId(), TransactionType.BUY, previous, amount.abs());
    history.save(entry);
    return Optional.of(entry);
  }

  private Optional<TransactionEntry> change(ProstUser receiver, ProstUser bearer,
      BigDecimal amount) {
    BigDecimal previous = receiver.getBalance();
    receiver.setBalance(amount);
    users.save(receiver);

    TransactionEntry entry = new TransactionEntry(null,
        receiver.getId(),
        bearer.getId(), TransactionType.CHANGE, previous, amount);
    history.save(entry);
    return Optional.of(entry);
  }

  public Page<TransactionEntry> getTransactions(int pageNumber, int pageSize, String receiverId) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("timestamp").descending());

    if (receiverId != null) {
      return history.findByReceiverId(receiverId, pageable);
    } else {
      return history.findAll(pageable);
    }
  }

  public Page<TransactionEntry> getPersonalTransactions(int pageNumber, int pageSize,
      String userId) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("timestamp").descending());

    return history.findByReceiverId(userId, pageable);
  }
}

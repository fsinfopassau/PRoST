package de.unipassau.fim.fsinfo.prost.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.data.repositories.TransactionRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

  @InjectMocks
  private TransactionService transactionService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TransactionRepository transactionRepository;

  private ProstUser receiver;
  private ProstUser bearer;
  private TransactionEntry transactionEntry;

  @BeforeEach
  public void setUp() {
    receiver = new ProstUser("receiver", "Receiver Name", "receiver@example.com", true);
    bearer = new ProstUser("bearer", "Bearer Name", "bearer@example.com", true);
    transactionEntry = new TransactionEntry(null, receiver.getId(), bearer.getId(),
        TransactionType.DEPOSIT, BigDecimal.ZERO, new BigDecimal("10.00"));
  }

  @Test
  public void testMoneyTransfer_InvalidAmountScale_ReturnsEmpty() {
    Optional<TransactionEntry> result = transactionService.moneyTransfer(Optional.of(receiver),
        Optional.of(receiver), Optional.of(bearer), new BigDecimal("10.001"),
        TransactionType.DEPOSIT);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testMoneyTransfer_ZeroAmount_ReturnsEmpty() {
    Optional<TransactionEntry> result = transactionService.moneyTransfer(Optional.of(receiver),
        Optional.of(receiver), Optional.of(bearer), new BigDecimal("0"),
        TransactionType.DEPOSIT);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testMoneyTransfer_NegativeAmount_ReturnsEmpty() {
    Optional<TransactionEntry> result = transactionService.moneyTransfer(Optional.of(receiver),
        Optional.of(receiver), Optional.of(bearer), new BigDecimal("-0.01"),
        TransactionType.DEPOSIT);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testMoneyTransfer_MissingReceiver_ReturnsEmpty() {
    Optional<TransactionEntry> result = transactionService.moneyTransfer(Optional.of(receiver),
        Optional.empty(), Optional.of(bearer), new BigDecimal("10.00"), TransactionType.DEPOSIT);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testMoneyTransfer_MissingBearer_ReturnsEmpty() {
    Optional<TransactionEntry> result = transactionService.moneyTransfer(Optional.of(receiver),
        Optional.of(bearer), Optional.empty(), new BigDecimal("10.00"), TransactionType.DEPOSIT);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testMoneyTransfer_Deposit_PositiveAmount() {
    when(transactionRepository.save(any(TransactionEntry.class))).thenReturn(transactionEntry);

    Optional<TransactionEntry> result = transactionService.moneyTransfer(Optional.empty(),
        Optional.of(receiver), Optional.of(bearer), new BigDecimal("10.00"),
        TransactionType.DEPOSIT);

    assertTrue(result.isPresent());
    assertEquals(receiver.getId(), result.get().getReceiverId());
    assertEquals(TransactionType.DEPOSIT, result.get().getTransactionType());
  }

  @Test
  public void testMoneyTransfer_Buy_PositiveAmount() {
    receiver.setBalance(new BigDecimal("20.00"));
    when(transactionRepository.save(any(TransactionEntry.class))).thenReturn(transactionEntry);

    Optional<TransactionEntry> result = transactionService.moneyTransfer(Optional.of(receiver),
        Optional.of(receiver), Optional.of(bearer), new BigDecimal("10.00"), TransactionType.BUY);

    assertTrue(result.isPresent());
    assertEquals(receiver.getId(), result.get().getReceiverId());
    assertEquals(TransactionType.BUY, result.get().getTransactionType());
  }

  @Test
  public void testMoneyTransfer_Buy_NegativeAmount() {
    receiver.setBalance(new BigDecimal("20.00"));

    Optional<TransactionEntry> result = transactionService.moneyTransfer(Optional.of(receiver),
        Optional.of(receiver), Optional.of(bearer), new BigDecimal("-10.00"), TransactionType.BUY);

    assertFalse(result.isPresent());
  }

  @Test
  public void testMoneyTransfer_Change_SetsBalance() {
    receiver.setBalance(new BigDecimal("20.00"));
    when(transactionRepository.save(any(TransactionEntry.class))).thenReturn(transactionEntry);

    Optional<TransactionEntry> result = transactionService.moneyTransfer(Optional.of(receiver),
        Optional.of(receiver), Optional.of(bearer), new BigDecimal("30.00"),
        TransactionType.CHANGE);

    assertTrue(result.isPresent());
    assertEquals(receiver.getId(), result.get().getReceiverId());
    assertEquals(TransactionType.CHANGE, result.get().getTransactionType());
  }

  @Test
  public void testGetTransactions_WithReceiverId() {
    PageRequest pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
    when(transactionRepository.findByReceiverId(receiver.getId(), pageable)).thenReturn(
        new PageImpl<>(List.of(transactionEntry)));

    Page<TransactionEntry> result = transactionService.getTransactions(0, 10, receiver.getId());

    assertFalse(result.isEmpty());
    assertEquals(1, result.getTotalElements());
    assertEquals(receiver.getId(), result.getContent().get(0).getReceiverId());
  }

  @Test
  public void testGetTransactions_WithoutReceiverId() {
    PageRequest pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
    when(transactionRepository.findAll(pageable)).thenReturn(
        new PageImpl<>(List.of(transactionEntry)));

    Page<TransactionEntry> result = transactionService.getTransactions(0, 10, null);

    assertFalse(result.isEmpty());
    assertEquals(1, result.getTotalElements());
    assertEquals(receiver.getId(), result.getContent().get(0).getReceiverId());
  }

  @Test
  public void testGetPersonalTransactions() {
    PageRequest pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
    when(transactionRepository.findByReceiverId("receiver", pageable)).thenReturn(
        new PageImpl<>(List.of(transactionEntry)));

    Page<TransactionEntry> result = transactionService.getPersonalTransactions(0, 10, "receiver");

    assertFalse(result.isEmpty());
    assertEquals(1, result.getTotalElements());
    assertEquals(receiver.getId(), result.getContent().get(0).getReceiverId());
  }
}

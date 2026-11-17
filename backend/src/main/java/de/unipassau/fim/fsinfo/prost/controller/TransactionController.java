package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.security.CustomUserDetailsContextMapper.CustomUserDetails;
import de.unipassau.fim.fsinfo.prost.service.BalanceConflictException;
import de.unipassau.fim.fsinfo.prost.service.TransactionService;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

  private final TransactionService transactionService;

  @Autowired
  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  /**
   * Sets the balance to an absolute value.
   *
   * @param expected optional (sent by the frontend): the balance the admin saw. If it changed in
   *                 the meantime (e.g. a purchase), nothing is changed and 409 is returned with the
   *                 current balance as body.
   */
  @PostMapping("/change")
  public ResponseEntity<String> balance(@RequestParam String id,
      @RequestParam String value, @RequestParam(required = false) String expected,
      @RequestParam(required = false) String requestId, Authentication authentication) {
    return transfer(id, value, expected, requestId, TransactionType.CHANGE, authentication);
  }

  /**
   * @param requestId optional client-generated id; repeating a request with the same id (double
   *                  click, retry) books it only once.
   */
  @PostMapping("/deposit")
  public ResponseEntity<String> deposit(@RequestParam String id,
      @RequestParam String value, @RequestParam(required = false) String requestId,
      Authentication authentication) {
    return transfer(id, value, null, requestId, TransactionType.DEPOSIT, authentication);
  }

  private ResponseEntity<String> transfer(String id, String value, String expected,
      String requestId, TransactionType type, Authentication authentication) {
    if (authentication == null || value == null || id == null) {
      return ResponseEntity.badRequest().build();
    }
    if (requestId != null && (requestId.isBlank() || requestId.length() > 64)) {
      return ResponseEntity.badRequest().body("invalid requestId");
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    BigDecimal money;
    BigDecimal expectedBalance;
    try {
      money = new BigDecimal(value);
      expectedBalance = expected == null ? null : new BigDecimal(expected);
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().build();
    }

    try {
      Optional<TransactionEntry> transaction = transactionService.moneyTransfer(
          Optional.empty(), id, userDetails.getUsername(), money, type, requestId,
          expectedBalance);
      return transaction.isPresent() ? ResponseEntity.ok().build()
          : ResponseEntity.badRequest().build();
    } catch (BalanceConflictException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(e.getCurrentBalance().toPlainString());
    } catch (DataAccessException | TransactionSystemException e) {
      // a concurrent duplicate of the same request was committed first (unique requestId)
      if (transactionService.findByRequestId(requestId).isPresent()) {
        return ResponseEntity.ok().build();
      }
      throw e;
    }
  }

  /**
   * @param p: Page-Number 0-MAX
   * @param s: Page-Size 1-MAX
   * @return Page + infos
   */
  @GetMapping("/list")
  public Page<TransactionEntry> getTransactions(
      @RequestParam(defaultValue = "0") int p,
      @RequestParam(defaultValue = "10") int s,
      @RequestParam(required = false) String receiverId) {
    return transactionService.getTransactions(Math.max(0, p), Math.min(Math.max(1, s), 100),
        receiverId);
  }

  /**
   * @param p: Page-Number 0-MAX
   * @param s: Page-Size 1-MAX
   * @return Page + infos
   */
  @GetMapping("/me")
  public Page<TransactionEntry> getPersonalTransactions(
      @RequestParam(defaultValue = "0") int p,
      @RequestParam(defaultValue = "10") int s,
      Authentication authentication) {
    if (authentication == null) {
      return Page.empty();
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return transactionService.getPersonalTransactions(Math.max(0, p), Math.min(Math.max(1, s), 100),
        userDetails.getUsername());
  }

}

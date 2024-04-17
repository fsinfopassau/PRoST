package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.security.CustomUserDetailsContextMapper.CustomUserDetails;
import de.unipassau.fim.fsinfo.prost.service.TransactionService;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

  @PostMapping("/change")
  public ResponseEntity<BigDecimal> balance(@RequestParam String id,
      @RequestParam String value, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    try {
      BigDecimal money = new BigDecimal(value);

      Optional<TransactionEntry> transaction = transactionService.moneyTransfer(
          Optional.empty(), id, userDetails.getUsername(), money, TransactionType.CHANGE);

      if (transaction.isPresent()) {
        return ResponseEntity.ok().build();
      }
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/deposit")
  public ResponseEntity<BigDecimal> transaction(@RequestParam String id,
      @RequestParam String value, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    try {
      BigDecimal money = new BigDecimal(value);

      Optional<TransactionEntry> transaction = transactionService.moneyTransfer(
          Optional.empty(), id, userDetails.getUsername(), money, TransactionType.DEPOSIT);

      if (transaction.isPresent()) {
        return ResponseEntity.ok().build();
      }
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.badRequest().build();
  }

  /**
   * @param p: Page-Number 0-MAX
   * @param s: Page-Size
   * @return Page + infos
   */
  @GetMapping("/list")
  public Page<TransactionEntry> getTransactions(
      @RequestParam(defaultValue = "0") int p,
      @RequestParam(defaultValue = "10") int s,
      @RequestParam(required = false) String userId) {
    return transactionService.getTransactions(Math.max(0, p), Math.max(1, s), userId);
  }

  /**
   * @param p: Page-Number 0-MAX
   * @param s: Page-Size
   * @return Page + infos
   */
  @GetMapping("/me")
  public Page<TransactionEntry> getPersonalTransactions(
      @RequestParam(defaultValue = "0") int p,
      @RequestParam(defaultValue = "10") int s,
      Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return transactionService.getPersonalTransactions(Math.max(0, p), Math.max(1, s),
        userDetails.getUsername());
  }

}

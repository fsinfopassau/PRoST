package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dto.InvoiceDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.service.InvoiceService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

  private final InvoiceService invoiceService;

  private final UserRepository users;

  @Autowired
  public InvoiceController(InvoiceService invoiceService, UserRepository users) {
    this.invoiceService = invoiceService;
    this.users = users;
  }

  /**
   * @param p: Page-Number 0-MAX
   * @param s: Page-Size
   * @return Page + infos
   */
  @GetMapping
  public Page<InvoiceDTO> getInvoices(
      @RequestParam(defaultValue = "0") int p,
      @RequestParam(defaultValue = "10") int s,
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) Boolean mailed) {
    return invoiceService.getInvoices(Math.max(0, p), Math.max(1, s), mailed, userId);
  }

  @PostMapping("/create")
  public ResponseEntity<List<String>> create(@RequestBody List<String> userIds) {
    Optional<List<String>> sentInvoices = invoiceService.createInvoices(userIds);
    return sentInvoices.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @DeleteMapping("/delete")
  public ResponseEntity<List<Long>> delete(@RequestBody List<Long> invoiceIds) {
    Optional<List<Long>> sentInvoices = invoiceService.delete(invoiceIds);
    return sentInvoices.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PostMapping("/mail")
  public ResponseEntity<List<Long>> mailToUser(@RequestBody List<Long> invoiceIds) {
    Optional<List<Long>> sentInvoices = invoiceService.mailInvoices(invoiceIds);
    return sentInvoices.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PostMapping("/publish")
  public ResponseEntity<List<Long>> publish(@RequestBody List<Long> invoiceIds) {
    Optional<List<Long>> sentInvoices = invoiceService.publish(invoiceIds);
    return sentInvoices.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

}

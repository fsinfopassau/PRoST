package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.dto.InvoiceDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.service.InvoiceService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

  @Autowired
  private InvoiceService invoiceService;

  @Autowired
  private UserRepository users;

  @PostMapping("/create/{id}")
  public ResponseEntity<InvoiceDTO> create(@PathVariable String id) {
    Optional<KdvUser> user = users.findById(id);

    if (user.isPresent()) {
      Optional<InvoiceDTO> invoice = invoiceService.createInvoice(user.get());

      if (invoice.isPresent()) {
        return ResponseEntity.ok(invoice.get());
      }
    }

    return ResponseEntity.badRequest().build();
  }

}

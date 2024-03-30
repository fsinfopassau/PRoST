package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.service.ChangeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Describes changes made by admin or others to the settings and money-transactions
 */
@RestController
@RequestMapping("/api/history/changes")
public class ChangeHistoryController {

  final ChangeHistoryService service;

  @Autowired
  public ChangeHistoryController(ChangeHistoryService service) {
    this.service = service;
  }

}

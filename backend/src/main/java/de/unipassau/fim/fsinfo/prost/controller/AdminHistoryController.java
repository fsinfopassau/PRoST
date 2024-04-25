package de.unipassau.fim.fsinfo.prost.controller;


import de.unipassau.fim.fsinfo.prost.data.dao.AdminHistoryEntry;
import de.unipassau.fim.fsinfo.prost.service.AdminHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history/admin")
public class AdminHistoryController {

  private final AdminHistoryService service;

  @Autowired
  public AdminHistoryController(AdminHistoryService service) {
    this.service = service;
  }


  /**
   * @param p: Page-Number 0-MAX
   * @param s: Page-Size 1-MAX
   * @return Page + infos
   */
  @GetMapping("/list")
  public Page<AdminHistoryEntry> getHistory(
      @RequestParam(defaultValue = "0") int p,
      @RequestParam(defaultValue = "10") int s,
      @RequestParam(required = false) String changer,
      @RequestParam(required = false) String type) {
    return service.findAll(p, s, changer, type);
  }

}

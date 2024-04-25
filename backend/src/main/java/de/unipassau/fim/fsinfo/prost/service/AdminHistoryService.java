package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.dao.AdminHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.repositories.AdminHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.type.AdminHistoryDomain;
import de.unipassau.fim.fsinfo.prost.data.type.AdminHistoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminHistoryService {

  private final AdminHistoryRepository history;

  @Autowired
  public AdminHistoryService(AdminHistoryRepository history) {
    this.history = history;
  }

  @Transactional
  public void addDeletion(String prev) {
    createEntry(AdminHistoryType.DELETE, prev, null);
  }

  @Transactional
  public void addNew(String next) {
    createEntry(AdminHistoryType.ADD, null, next);
  }

  @Transactional
  public void addChange(String prev, String next) {
    createEntry(AdminHistoryType.CHANGE, prev, next);
  }

  private void createEntry(AdminHistoryType type, String prev, String next) {
    AdminHistoryEntry entry = new AdminHistoryEntry(type,
        AdminHistoryDomain.UNDEFINED); // TODO replace Domain
    entry.setPreviousValue(prev);
    entry.setNewValue(next);
    history.save(entry);
  }

  public Page<AdminHistoryEntry> findAll(int p, int s, String changer, String type) {
    return null; // TODO
  }

}

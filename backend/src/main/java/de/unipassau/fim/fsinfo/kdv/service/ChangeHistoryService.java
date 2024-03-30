package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.ChangeHistoryEntry;
import de.unipassau.fim.fsinfo.kdv.data.dao.ChangeHistoryEntry.ChangeType;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ChangeHistoryRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeHistoryService {

  private final ChangeHistoryRepository history;

  @Autowired
  public ChangeHistoryService(ChangeHistoryRepository history) {
    this.history = history;
  }

  @Transactional
  public Long startChange(ChangeType type, Object value) {
    ChangeHistoryEntry entry = new ChangeHistoryEntry(type, value.toString());
    history.save(entry);
    return entry.getId();
  }

  @Transactional
  public boolean finishChange(Long id) {
    Optional<ChangeHistoryEntry> entryOptional = history.findById(id);
    if (entryOptional.isEmpty()) {
      return false;
    }

    ChangeHistoryEntry entry = entryOptional.get();
    entry.setCompleted(true);
    history.save(entry);

    return true;
  }

}

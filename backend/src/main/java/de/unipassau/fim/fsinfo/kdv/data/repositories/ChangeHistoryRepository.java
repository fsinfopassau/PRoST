package de.unipassau.fim.fsinfo.kdv.data.repositories;

import de.unipassau.fim.fsinfo.kdv.data.dao.ChangeHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeHistoryRepository extends JpaRepository<ChangeHistoryEntry, Long> {

}

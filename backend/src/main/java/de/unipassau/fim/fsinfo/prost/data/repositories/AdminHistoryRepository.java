package de.unipassau.fim.fsinfo.prost.data.repositories;

import de.unipassau.fim.fsinfo.prost.data.dao.AdminHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminHistoryRepository extends JpaRepository<AdminHistoryEntry, Long> {

}

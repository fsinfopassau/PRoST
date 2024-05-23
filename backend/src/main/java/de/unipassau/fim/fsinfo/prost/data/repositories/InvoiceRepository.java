package de.unipassau.fim.fsinfo.prost.data.repositories;

import de.unipassau.fim.fsinfo.prost.data.dao.InvoiceEntry;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<InvoiceEntry, Long> {

  List<InvoiceEntry> findByUserIdEqualsOrderByTimestampDesc(String username);

  Page<InvoiceEntry> findByUserId(String userId, Pageable pageable);

  Page<InvoiceEntry> findByUserIdAndMailedFalse(String userId,
      Pageable pageable);

  Page<InvoiceEntry> findByUserIdAndMailedTrue(String userId,
      Pageable pageable);

  Page<InvoiceEntry> findByMailedFalse(Pageable pageable);

  Page<InvoiceEntry> findByMailedTrue(Pageable pageable);

  List<InvoiceEntry> findByUserIdAndTimestampGreaterThanOrderByTimestampAsc(String userId,
      Long startDate);

}

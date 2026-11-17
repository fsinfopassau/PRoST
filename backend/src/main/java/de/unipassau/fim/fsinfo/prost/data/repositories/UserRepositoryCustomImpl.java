package de.unipassau.fim.fsinfo.prost.data.repositories;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public void lockForUpdate(ProstUser user) {
    // refresh (instead of a locking query) so an instance that was already loaded earlier in
    // this transaction gets its state re-read after the lock is acquired, not kept stale.
    entityManager.refresh(user, LockModeType.PESSIMISTIC_WRITE);
  }
}

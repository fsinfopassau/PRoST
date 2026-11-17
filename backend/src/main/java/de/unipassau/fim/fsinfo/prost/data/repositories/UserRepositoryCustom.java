package de.unipassau.fim.fsinfo.prost.data.repositories;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;

public interface UserRepositoryCustom {

  /**
   * Re-reads the given (managed) user from the database and holds a row lock
   * (SELECT ... FOR UPDATE) until the surrounding transaction ends. Must be called inside a
   * transaction before reading and modifying the balance, so concurrent transfers on the same
   * user are serialized instead of overwriting each other.
   */
  void lockForUpdate(ProstUser user);
}

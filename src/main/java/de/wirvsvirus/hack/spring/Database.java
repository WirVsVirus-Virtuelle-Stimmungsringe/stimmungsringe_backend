package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.repository.microstream.DataRoot;

public interface Database {

  /**
   * calls to dataRoot are cheap
   *
   * CAUTION: do not store/reuse the reference returned
   * <p>
   * subsequent calls typically return the same instance
   */
  DataRoot dataRoot();

  void persist(Object instance);

}

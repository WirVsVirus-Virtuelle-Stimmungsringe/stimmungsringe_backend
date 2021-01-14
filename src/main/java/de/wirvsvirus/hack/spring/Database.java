package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.repository.microstream.DataRoot;

public interface Database {

  /**
   * CAUTION: do not reuse the reference returned
   *
   * subsequent calls typically return the same instance
   */
  DataRoot dataRoot();

  void persist(Object... objects);

}

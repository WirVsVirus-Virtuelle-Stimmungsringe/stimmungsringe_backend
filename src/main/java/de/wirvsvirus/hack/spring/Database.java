package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.repository.microstream.DataRoot;

public interface Database {

  /**
   * CAUTION: do not reuse the reference returned
   */
  DataRoot reloadRoot();

  void persist(Object... objects);

}

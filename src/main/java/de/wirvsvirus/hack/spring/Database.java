package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.repository.microstream.DataRoot;

public interface Database {

  /**
   * CAUTION: do not reuse the reference returned
   * <p>
   * subsequent calls typically return the same instance
   */
  DataRoot dataRoot();

  void persist(Object instance);

  // atm no usecase for convenience methods from https://manual.docs.microstream.one/data-store/storing-data/convenience-methods-and-explicit-storing-transactions

//  void persist(Iterable<?> instances);

//  void persist(Object... instances);

}

package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.repository.microstream.DataRoot;
import java.util.Collection;
import java.util.Map;

public interface Database {

  /**
   * CAUTION: do not reuse the reference returned
   * <p>
   * subsequent calls typically return the same instance
   */
  DataRoot dataRoot();

  void persist(Object instance);

  void persist(Iterable<?> instances);

  void persist(Object... instances);

}

package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.model.AggregateRoot;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Database {

  /**
   * CAUTION: do not reuse the reference returned
   *
   * subsequent calls typically return the same instance
   */
  DataRoot dataRoot();

  void persist(AggregateRoot aggregateRoot);

  void persist(Collection<? extends AggregateRoot> aggregateRoots);

  void persist(Map<?, ?> map);

  /**
   * use for map/list, etc
   * @param objects
   * @param <T>
   */
  // TODO remove/restrict
  <T extends AggregateRoot> void persistAny(Object... objects);


}

package de.wirvsvirus.hack.spring;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.AggregateRoot;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import java.util.Collection;
import java.util.Map;
import one.microstream.storage.types.StorageManager;

public class DatabaseAccessImpl implements Database {

  private final StorageManager storageManager;

  public DatabaseAccessImpl(StorageManager storageManager) {
    this.storageManager = storageManager;
  }

  @Override
  public DataRoot dataRoot() {
    return (DataRoot) storageManager.root();
  }

  @Override
  public void persist(AggregateRoot aggregateRoots) {
    storageManager.store(aggregateRoots);
  }

  @Override
  public void persist(Collection<? extends AggregateRoot> aggregateRoots) {
    storageManager.storeAll(aggregateRoots);
  }

  @Override
  public void persist(Map<?, ? extends AggregateRoot> map) {
    Preconditions.checkNotNull(map);
    storageManager.store(map);
    storageManager.storeAll(map.values());
  }

  @Override
  public void persistAny(Object... objects) {
    storageManager.storeAll(objects);
  }

  // TODO remove
  @Override
  public void persistAnyMap(Map<?, ?> anyMap) {
    Preconditions.checkNotNull(anyMap);
    storageManager.store(anyMap);
    storageManager.store(anyMap.values()); // note: may fail if elements are collections/maps/etc.
  }
}

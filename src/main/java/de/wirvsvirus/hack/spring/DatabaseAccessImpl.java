package de.wirvsvirus.hack.spring;

import com.google.common.base.Preconditions;
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
  public void persist(Object instance) {
    storageManager.store(instance);
  }

  @Override
  public void persist(Iterable<?> instances) {
    storageManager.storeAll(instances);
  }

  @Override
  public void persist(Object... instances) {
    storageManager.storeAll(instances);
  }

}

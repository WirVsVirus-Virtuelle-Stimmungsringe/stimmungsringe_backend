package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.repository.microstream.DataRoot;
import one.microstream.storage.types.StorageManager;

class DatabaseAccessImpl implements Database {

  private final StorageManager storageManager;

  DatabaseAccessImpl(StorageManager storageManager) {
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

}

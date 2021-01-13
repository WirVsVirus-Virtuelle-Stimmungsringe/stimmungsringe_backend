package de.wirvsvirus.hack.microstream;

import de.wirvsvirus.hack.repository.microstream.DataRoot;
import de.wirvsvirus.hack.spring.MicrostreamConfiguration;
import one.microstream.storage.types.EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageManager;

public class FixupMicro {

  public static void main(String[] args) {

    final EmbeddedStorageManager storageManager = EmbeddedStorage.start(
        MicrostreamConfiguration.STORAGE_PATH
    );

    final DataRoot dataRoot = (DataRoot) storageManager.root();

    System.out.println("migration " + dataRoot.getMigrationMetadata());

    dataRoot.dumpToSysout();


    dataRoot.getMigrationMetadata().setMockDataCreated(false);
//    storageManager.

  }

}

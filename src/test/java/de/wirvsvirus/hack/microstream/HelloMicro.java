package de.wirvsvirus.hack.microstream;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Data;
import one.microstream.persistence.internal.InquiringLegacyTypeMappingResultor;
import one.microstream.persistence.internal.PrintingLegacyTypeMappingResultor;
import one.microstream.persistence.types.PersistenceLegacyTypeMappingResultor;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

public class HelloMicro {

  public static final Path STORAGE_PATH = Paths.get("/tmp/familiarise-micro-hello7");

  @Data
  static class Root {
    String blabla;
    String name2;
  }

  public static void main(String[] args) {
    // Initialize a storage manager ("the database") with purely defaults.
    final EmbeddedStorageFoundation<?> foundation = EmbeddedStorage.Foundation(STORAGE_PATH);
    foundation.getConnectionFoundation().setLegacyTypeMappingResultor(
        PrintingLegacyTypeMappingResultor.New(
            InquiringLegacyTypeMappingResultor.New(
                PersistenceLegacyTypeMappingResultor.New(),
                0.2f
            )
        )
        );
    final EmbeddedStorageManager storageManager = foundation.createEmbeddedStorageManager().start();

    if (storageManager.root() == null) {
      final Root root = new Root();
      storageManager.setRoot(root);
      storageManager.storeRoot();
    }

    // https://manual.docs.microstream.one/data-store/legacy-type-mapping

    final Root dataRoot = (Root) storageManager.root();

    System.out.println("current value1: " + dataRoot.getBlabla());
    System.out.println("current value2: " + dataRoot.getName2());

//    dataRoot.setName1("hello world");
//    storageManager.storeRoot();


    storageManager.close();

//    {
//      System.out.println("restart");
//      final EmbeddedStorageManager storageManagerReload = MicrostreamConfiguration.createStorageManager(
//          STORAGE_PATH
//      );
//
//      final Root reloaded = (Root) storageManagerReload.root();
//
//      storageManagerReload.close();
//    }

  }

}

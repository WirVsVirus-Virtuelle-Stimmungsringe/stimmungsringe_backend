package de.wirvsvirus.hack.microstream;

import de.wirvsvirus.hack.mock.InMemoryDatastore;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;
import one.microstream.storage.types  .EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageManager;

public class HelloMicro {

  public static void main(String[] args) {
    // Initialize a storage manager ("the database") with purely defaults.
    final EmbeddedStorageManager storageManager = EmbeddedStorage.start(
        Paths.get("/tmp/familiarise-micro-hello")
    );

    if (storageManager.root() == null) {
      storageManager.setRoot(new DataRoot());
    }

    DataRoot dataRoot = (DataRoot) storageManager.root();

    dataRoot.getSentimentByUser().put(UUID.randomUUID(), Sentiment.cloudy);
    storageManager.storeRoot();

    System.out.println("- " + dataRoot.getSentimentByUser().size());



// print the last loaded root instance,
// replace it with a current version and store it
    storageManager.shutdown();
  }

}

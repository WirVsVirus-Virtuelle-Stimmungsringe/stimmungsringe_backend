package de.wirvsvirus.hack.microstream;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.With;
import one.microstream.jdk8.java.util.BinaryHandlersJDK8;
import one.microstream.storage.types  .EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageManager;

public class HelloMicro {

  @Data
  static class Sub {
    String name;
  }

  @Data
  static class Root {
    String name;
    List<String> tags;
    Sub sub;
    Instant timestamp;
  }

  public static void main(String[] args) {
    // Initialize a storage manager ("the database") with purely defaults.
    final EmbeddedStorageManager storageManager = EmbeddedStorage.start(
        Paths.get("/tmp/familiarise-micro-hello2")
    );

    if (storageManager.root() == null) {
      final Root root = new Root();
      root.setTags(new ArrayList<>());
      storageManager.setRoot(root);
      storageManager.storeRoot();
    }

    Root root = (Root) storageManager.root();

//    root.setTimestamp(Instant.now());
//    storageManager.storeRoot();

    System.out.println("root=" + root.getName());
    System.out.println("tags=" + root.getTags());
    System.out.println("timestamp=" + root.getTimestamp());
//    System.out.println("name=" + root.getSub().getName());

//    root.withTags()


    storageManager.shutdown();
  }

}

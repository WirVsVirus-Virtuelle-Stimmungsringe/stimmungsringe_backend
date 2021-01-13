package de.wirvsvirus.hack.microstream;

import java.nio.file.Paths;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import one.microstream.storage.types  .EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageManager;

public class HelloMicro {

  @With
  @Value
  static class Sub {
    String name;
  }

  @With
  @Value
  @Builder
  static class Root {
    String name;
    List<String> tags;
    Sub sub;
  }

  public static void main(String[] args) {
    // Initialize a storage manager ("the database") with purely defaults.
    final EmbeddedStorageManager storageManager = EmbeddedStorage.start(
        Paths.get("/tmp/familiarise-micro-hello2")
    );

    if (storageManager.root() == null) {
//      storageManager.setRoot(new Root());
    }

    Root root = (Root) storageManager.root();

    System.out.println("root=" + root.getName());
    System.out.println("tags=" + root.getTags());
    System.out.println("name=" + root.getSub().getName());

//    root.withTags()


    storageManager.shutdown();
  }

}

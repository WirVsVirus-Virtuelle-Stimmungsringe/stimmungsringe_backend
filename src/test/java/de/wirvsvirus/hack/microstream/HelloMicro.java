package de.wirvsvirus.hack.microstream;

import de.wirvsvirus.hack.spring.DatabaseAccessImpl;
import de.wirvsvirus.hack.spring.MicrostreamConfiguration;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import one.microstream.storage.types.EmbeddedStorageManager;

public class HelloMicro {

  public static final Path STORAGE_PATH = Paths.get("/tmp/familiarise-micro-hello5");

  @Data
  static class Sub {
    String name;
  }

  @Data
  static class Root {
    String name;
    List<String> tags;
    Sub sub;
    Map<String, List<Sub>> subMap;
    Instant timestamp;
  }

  public static void main(String[] args) {
    // Initialize a storage manager ("the database") with purely defaults.
    final EmbeddedStorageManager storageManager = MicrostreamConfiguration.createStorageManager(
        STORAGE_PATH
    );

    if (storageManager.root() == null) {
      final Root root = new Root();
      root.setTags(new ArrayList<>());
      storageManager.setRoot(root);
      storageManager.storeRoot();
    }

    final DatabaseAccessImpl database = new DatabaseAccessImpl(storageManager);

    Root root = (Root) storageManager.root();

//    root.setTimestamp(Instant.now());
//    storageManager.storeRoot();

    System.out.println("root=" + root.getName());
    System.out.println("tags=" + root.getTags());
    System.out.println("timestamp=" + root.getTimestamp());

    if (root.getSubMap() == null) {
      root.setSubMap(new HashMap<>());
//      storageManager.store(root.getSubMap());
    }
    if (!root.getSubMap().containsKey("foo")) {
      root.getSubMap().put("foo", new ArrayList<>());
//      storageManager.store(root.getSubMap());
    }

    final Sub time = new Sub();
    time.setName("xx " + Instant.now().toString());
    root.getSubMap().get("foo").add(time);

    root.getSubMap().get("foo").forEach(e -> {
      e.setName(e.getName() + "*");
      database.persist(e);
    });

//    storageManager.store(root.getSubMap());
//    storageManager.storeAll(root.getSubMap().values());

    database.persist(root.getSubMap());


    storageManager.close();

    {
      System.out.println("restart");
      final EmbeddedStorageManager storageManagerReload = MicrostreamConfiguration.createStorageManager(
          STORAGE_PATH
      );

      final Root reloaded = (Root) storageManagerReload.root();
      reloaded.getSubMap().keySet().forEach(k -> System.out.println("k " + k));
      reloaded.getSubMap().get("foo").forEach(z -> {
        System.out.println("- " + z.getName());
      });

      storageManagerReload.close();
    }


  }

}

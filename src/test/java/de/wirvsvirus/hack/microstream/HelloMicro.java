package de.wirvsvirus.hack.microstream;

import com.google.common.collect.Lists;
import de.wirvsvirus.hack.mock.InMemoryDatastore;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.With;
import lombok.experimental.Wither;
import one.microstream.storage.types  .EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageManager;
import org.apache.commons.lang3.StringUtils;

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
      storageManager.setRoot(new Root());
    }

    Root root = (Root) storageManager.root();

    System.out.println("root=" + root.getName());
    System.out.println("tags=" + root.getTags());
    System.out.println("name=" + root.getSub().getName());

    root.withTags()


    storageManager.shutdown();
  }

}

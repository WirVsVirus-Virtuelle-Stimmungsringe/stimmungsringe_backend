package de.wirvsvirus.hack.repository.microstream;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.User;
import java.nio.file.Paths;
import one.microstream.storage.types.EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageManager;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Microstream {

  private static final EmbeddedStorageManager STORAGE_MANAGER;

  static {
    STORAGE_MANAGER = EmbeddedStorage.start(
        Paths.get("/tmp/familiarise-micro")
    );
    restoreFromStorage();
  }

  public static DataRoot dataRoot;

  private static void restoreFromStorage() {
    final Object root = STORAGE_MANAGER.root();
    Preconditions.checkNotNull(root, "Root missing!");
    dataRoot = (DataRoot) root;
  }

  public static void writeDataToStorage() {
   // note: maps/lists will not be stored along with their root as the collection instance was not changed
    STORAGE_MANAGER.storeRoot();

    STORAGE_MANAGER.store(dataRoot.getAllUsers());
    STORAGE_MANAGER.store(dataRoot.getAllGroups());
    STORAGE_MANAGER.store(dataRoot.getGroupByUserId());
    STORAGE_MANAGER.store(dataRoot.getSentimentByUser());
    STORAGE_MANAGER.store(dataRoot.getLastStatusUpdateByUser());
    STORAGE_MANAGER.store(dataRoot.getAllGroupMessages());
    STORAGE_MANAGER.store(dataRoot.getAllDevicesByUser());
  }

  public static void inspect() {
    final DataRoot reloaded = (DataRoot) STORAGE_MANAGER.root();

    for (User user : reloaded.getAllUsers().values()) {
      System.out.println("- M " + user.getName());
    }

  }

}

package de.wirvsvirus.hack.spring;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import de.wirvsvirus.hack.repository.microstream.MigrationMetadata;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import one.microstream.storage.types.EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageManager;
import one.microstream.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("microstream")
public class MicrostreamConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(MicrostreamConfiguration.class);
  public static final Path STORAGE_PATH = Paths.get(
      "/Users/stefanostermayr/Documents/wirvsvirus_hackathon/microstream-familiarise-db/stefan");

  @Bean
  public EmbeddedStorageManager storageManager() {
    final EmbeddedStorageManager storageManager = EmbeddedStorage.start(
        STORAGE_PATH
    );
    return storageManager;
  }

  @Bean
  public Database database(final StorageManager storageManager) {
    final DataRoot dataRoot;
    if (storageManager.root() == null) {
      bootstrapDataRoot(storageManager);
      dataRoot = (DataRoot) storageManager.root();
    } else {
      dataRoot = (DataRoot) storageManager.root();
    }

    if (dataRoot.getMigrationMetadata() == null) {
      dataRoot.setMigrationMetadata(new MigrationMetadata());
      storageManager.store(dataRoot);
    }

    dataRoot.dumpToSysout();

    return new Database() {
      @Override
      public DataRoot reloadRoot() {
        return (DataRoot) storageManager.root();
      }

      @Override
      public void persist(Object... objects) {
        storageManager.storeAll(objects);
      }
    };
  }

  private void bootstrapDataRoot(final StorageManager storageManager) {
    LOGGER.info("Bootstrapping database ({})...", storageManager.configuration().fileProvider().getStorageLocationIdentifier());

    final DataRoot newDataRoot = new DataRoot();

    newDataRoot.setAllUsers(new HashMap<>());
    newDataRoot.setAllGroups(new HashMap<>());
    newDataRoot.setGroupByUserId(new HashMap<>());
    newDataRoot.setSentimentByUser(new HashMap<>());
    newDataRoot.setLastStatusUpdateByUser(new HashMap<>());
    newDataRoot.setAllDevicesByUser(new HashMap<>());
    newDataRoot.setAllGroupMessages(new HashMap<>());

    storageManager.setRoot(newDataRoot);
    storageManager.storeRoot();
  }

}

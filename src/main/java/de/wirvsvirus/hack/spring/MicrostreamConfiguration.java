package de.wirvsvirus.hack.spring;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.AggregateRoot;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import de.wirvsvirus.hack.repository.microstream.MigrationMetadata;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import one.microstream.jdk8.java.util.BinaryHandlersJDK8;
import one.microstream.storage.types.EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageFoundation;
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
      "/Users/stefanostermayr/Documents/wirvsvirus_hackathon/microstream-familiarise-db/stefan/familiarise-v2");

  @Bean
  public EmbeddedStorageManager storageManager() {

    final EmbeddedStorageManager storageManager = createStorageManager(STORAGE_PATH);
    return storageManager;
  }

  public static EmbeddedStorageManager createStorageManager(Path storagePath) {
    // from https://github.com/microstream-one/bookstore-demo/blob/master/src/main/java/one/microstream/demo/bookstore/BookStoreDemo.java
    final one.microstream.storage.configuration.Configuration configuration = one.microstream.storage.configuration.Configuration.Default();
    configuration.setBaseDirectory(storagePath.toString());
    // https://github.com/microstream-one/bookstore-demo/issues/1
//    configuration.setChannelCount(Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1));

    final EmbeddedStorageFoundation<?> foundation = configuration.createEmbeddedStorageFoundation();
    foundation.onConnectionFoundation(BinaryHandlersJDK8::registerJDK8TypeHandlers);
    final EmbeddedStorageManager storageManager = foundation.createEmbeddedStorageManager().start();
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
      public DataRoot dataRoot() {
        return (DataRoot) storageManager.root();
      }

      @Override
      public void persist(AggregateRoot aggregateRoots) {
        storageManager.store(aggregateRoots);
      }

      @Override
      public void persist(Collection<? extends AggregateRoot> aggregateRoots) {
        storageManager.storeAll(aggregateRoots);
      }

      @Override
      public void persist(Map<?, ?> map) {
        Preconditions.checkNotNull(map);
        storageManager.store(map);
        storageManager.storeAll(map.values());
      }

      @Override
      public void persistAny(Object... objects) {
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

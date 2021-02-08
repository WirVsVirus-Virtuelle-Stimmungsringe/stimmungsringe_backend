package de.wirvsvirus.hack.spring;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import de.wirvsvirus.hack.repository.microstream.MigrationMetadata;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import one.microstream.jdk8.java.util.BinaryHandlersJDK8;
import one.microstream.persistence.binary.types.Binary;
import one.microstream.persistence.internal.InquiringLegacyTypeMappingResultor;
import one.microstream.persistence.internal.PrintingLegacyTypeMappingResultor;
import one.microstream.persistence.types.PersistenceLegacyTypeHandler;
import one.microstream.persistence.types.PersistenceLegacyTypeMappingResultor;
import one.microstream.storage.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.EmbeddedStorageManager;
import one.microstream.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("microstream")
public class MicrostreamConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(MicrostreamConfiguration.class);

  private static final Path STORAGE_ROOT_MARKER_FILE = Paths.get(".microstream-root");

  /**
   * point to storage root directory
   * must contain marker file ".microstream-root"
   *
   * structure below: wirvsvirus-vNNN
   */
  @Value("${backend.microstream.storage-path}")
  private Path storagePath;

  @Value("${backend.microstream.storage-version}")
  private int storageCurrentVersion;

  @Bean
  public EmbeddedStorageManager storageManager() {

    Preconditions.checkState(
        Files.isRegularFile(storagePath.resolve(STORAGE_ROOT_MARKER_FILE)),
        "Microstream storage path <%s> must contain marker file <%s>",
        storagePath, STORAGE_ROOT_MARKER_FILE);

    final Path fullPath = storagePath.resolve(Paths.get(
        "familiarise-v" + storageCurrentVersion));

    LOGGER.info("Using storage path {}", fullPath);

    final EmbeddedStorageManager storageManager = createStorageManager(fullPath);
    return storageManager;
  }

  public static EmbeddedStorageManager createStorageManager(Path storagePath) {
    // from https://github.com/microstream-one/bookstore-demo/blob/master/src/main/java/one/microstream/demo/bookstore/BookStoreDemo.java
    final one.microstream.storage.configuration.Configuration configuration = one.microstream.storage.configuration.Configuration.Default();
    configuration.setBaseDirectory(storagePath.toString());
    // https://github.com/microstream-one/bookstore-demo/issues/1
//    configuration.setChannelCount(Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1));

    final EmbeddedStorageFoundation<?> foundation = configuration.createEmbeddedStorageFoundation();
    foundation.onConnectionFoundation(
        f -> {
          BinaryHandlersJDK8.registerJDK8TypeHandlers(f);
//          f.setLegacyTypeMappingResultor(
//              PrintingLegacyTypeMappingResultor.New(PersistenceLegacyTypeMappingResultor.New()));
        });
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

    return new DatabaseAccessImpl(storageManager);
  }

  private void bootstrapDataRoot(final StorageManager storageManager) {
    LOGGER.info("Bootstrapping database ({})...", storageManager.configuration().fileProvider().getStorageLocationIdentifier());

    final DataRoot newDataRoot = new DataRoot();

    newDataRoot.setAllUsers(new HashMap<>());
    newDataRoot.setAllGroups(new HashMap<>());
    newDataRoot.setGroupByUserId(new HashMap<>());
    newDataRoot.setStatusByUser(new HashMap<>());
    newDataRoot.setAllDevicesByUser(new HashMap<>());
    newDataRoot.setAllGroupMessages(new HashMap<>());

    storageManager.setRoot(newDataRoot);
    storageManager.storeRoot();
  }

}

package de.wirvsvirus.hack.repository.microstream;

import de.wirvsvirus.hack.spring.MicrostreamConfiguration;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import one.microstream.afs.nio.NioFileSystem;
import one.microstream.storage.types.EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageManager;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MicrostreamBackupService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MicrostreamBackupService.class);

  @Value("${backend.microstream.backup-path:}")
  private Path backupPath;

  @Autowired
  private EmbeddedStorageManager storageManager;

  /**
   * incremental backup (./monday/22)
   */
  @Scheduled(cron = "1 */15 7-23 * * *")
  public void performBackup() {
    if (backupPath == null) {
      LOGGER.info("No microstream backup path configured!");
      return;
    }

    final Path fullPath = backupPath.resolve(leveledBackupDirName());

    storageManager.issueFullBackup(
        NioFileSystem.New().ensureDirectoryPath(
            StreamEx.of(fullPath).map(Path::toString).toArray(new String[]{}))
    );
    LOGGER.info("Performed full microstream backup to <{}>", fullPath);
  }

  private Path leveledBackupDirName() {
    return Paths.get(
        LocalDate.now().getDayOfWeek().toString().toLowerCase(),
        Integer.toString(LocalTime.now().getHour())
    );
  }

}

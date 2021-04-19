package de.wirvsvirus.hack.repository.microstream;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import one.microstream.afs.nio.NioFileSystem;
import one.microstream.storage.types.EmbeddedStorageManager;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MicrostreamBackupService {

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
      log.info("No microstream backup path configured!");
      return;
    }

    final Path fullPath = backupPath.resolve(leveledBackupDirName());

    storageManager.issueFullBackup(
        NioFileSystem.New().ensureDirectoryPath(
            StreamEx.of(fullPath).map(Path::toString).toArray(new String[]{}))
    );
    log.info("Performed full microstream backup to <{}>", fullPath);
  }

  private Path leveledBackupDirName() {
    return Paths.get(
        LocalDate.now().getDayOfWeek().toString().toLowerCase(),
        Integer.toString(LocalTime.now().getHour())
    );
  }

}

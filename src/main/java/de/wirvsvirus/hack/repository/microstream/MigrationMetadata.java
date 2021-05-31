package de.wirvsvirus.hack.repository.microstream;

import de.wirvsvirus.hack.model.MicrostreamObject;
import lombok.Data;

@Data
public class MigrationMetadata implements MicrostreamObject {

  boolean mockDataCreated;

  boolean userStatusMapInitialized;

  boolean userStatusMapPopulatedFromExistingUsers;

  boolean lastSigninInitialized;

  @Deprecated
  boolean sentimentTextInitialized;

  boolean sentimentTextInitialized2;

  boolean staleMessagesDeleted;

}

package de.wirvsvirus.hack.repository.microstream;

import lombok.Data;

@Data
public class MigrationMetadata {

  boolean mockDataCreated;

  boolean userStatusMapInitialized;

  boolean userStatusMapPopulatedFromExistingUsers;

  boolean lastSigninInitialized;

  @Deprecated
  boolean sentimentTextInitialized;

  boolean sentimentTextInitialized2;

}

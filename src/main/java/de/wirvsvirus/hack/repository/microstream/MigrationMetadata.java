package de.wirvsvirus.hack.repository.microstream;

import lombok.Data;

/**
 * implement actual migration code in DatabaseMigration
 * consider revise createNewUser
 */
@Data
public class MigrationMetadata {

  boolean mockDataCreated;

  boolean userStatusMapInitialized;

  boolean userStatusMapPopulatedFromExistingUsers;

  boolean lastSigninInitialized;

  @Deprecated
  boolean sentimentTextInitialized;

  boolean sentimentTextInitialized2;

  boolean kickVotesInitialized;

}

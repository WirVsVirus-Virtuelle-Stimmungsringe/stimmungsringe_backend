package de.wirvsvirus.hack.repository.microstream;

import de.wirvsvirus.hack.model.MicrostreamObject;
import lombok.Data;

@Data
public class MigrationMetadata implements MicrostreamObject {

  boolean mockDataCreated;

  boolean userStatusMapInitialized;

  boolean userStatusMapPopulatedFromExistingUsers;

  @Deprecated
  boolean lastSigninInitialized;

  @Deprecated
  boolean sentimentTextInitialized;

  boolean sentimentTextInitialized2;

  boolean staleMessagesDeleted;

  @Deprecated
  boolean lastSigninInitialized2;

  boolean lastSigninInitialized3;

  boolean lastSigninInitialized4;

  boolean groupCreatedAtInitialized;

  boolean historyUserStatusChangesInitialized;

  boolean historyUserGroupMembershipInitialized;

  boolean historyOfGroupMembershipInitialized;

  boolean achievementShownStatusByUserInitialized;

}

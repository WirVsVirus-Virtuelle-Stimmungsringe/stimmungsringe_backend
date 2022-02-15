package de.wirvsvirus.hack.repository;

import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserStatusChangeHistory;
import java.util.List;

public interface HistoryQueryRepository {

  List<UserStatusChangeHistory> getHistoryOfStatusChanges();

  List<UserGroupMembershipHistory> getHistoryUserGroupMemberships();

}

package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.HistoryObject;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import de.wirvsvirus.hack.model.UserStatusChangeHistory;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class StatsServiceTest extends AbstractStatsTest {

  @Test
  public void sunnyTime() {

    joinGroup(t0.plusSeconds(0));
    updateStatus(t0.plusSeconds(5), Sentiment.sunny, Sentiment.sunnyWithClouds);
    updateStatus(t0.plusSeconds(10), Sentiment.cloudy, Sentiment.sunny);
    leaveGroup(t0.plusSeconds(30));
    updateStatus(t0.plusSeconds(45), Sentiment.sunny, Sentiment.cloudy);

    printHistory();

    calcSunshineHoursInGroup();
  }


  private void calcSunshineHoursInGroup() {

    // intersect group membership with sunny status

  }

}

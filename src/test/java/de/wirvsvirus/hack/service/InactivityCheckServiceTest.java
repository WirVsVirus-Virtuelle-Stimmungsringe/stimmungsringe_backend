package de.wirvsvirus.hack.service;

import static de.wirvsvirus.hack.service.InactivityCheckService.buildNoStatusUpdateString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

class InactivityCheckServiceTest {

  @Test
  void buildWithoutNames() {
    assertEquals("Wie geht es dir gerade!",
        buildNoStatusUpdateString(Lists.newArrayList()));
  }

  @Test
  void buildFromOne() {
    assertEquals("Dani möchte wissen, wie es dir geht!",
        buildNoStatusUpdateString(Lists.newArrayList("Dani")));
  }

  @Test
  void buildFromTwo() {
    assertEquals("Dani und Gudrun möchten wissen, wie es dir geht!",
        buildNoStatusUpdateString(Lists.newArrayList("Dani", "Gudrun")));
  }

  @Test
  void buildFromThree() {
    assertEquals("Blubb, Dani und Gudrun möchten wissen, wie es dir geht!",
        buildNoStatusUpdateString(Lists.newArrayList("Blubb", "Dani", "Gudrun")));
  }

  @Test
  void buildFromMoreThanThree() {
    assertEquals("Blubb, Dani und Gudrun möchten wissen, wie es dir geht!",
        buildNoStatusUpdateString(Lists.newArrayList("Blubb", "Dani", "Gudrun", "Foo", "Bar")));
  }

}
package de.wirvsvirus.hack.service;

import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IntervalIntersectionUtilTest {

  @Test
  void baseAlgo() {

    // see https://www.geeksforgeeks.org/find-intersection-of-intervals-given-by-two-lists/

    final List<Pair<Long, Long>> result = IntervalIntersectionUtil.intersectListOfLongs(
        ImmutableList.of(r(0, 4), r(5, 10), r(13, 20), r(24, 25)),
        ImmutableList.of(r(1, 5), r(8, 12), r(15, 24), r(25, 26))
    );

    Assertions.assertEquals(
        ImmutableList.of(r(1, 4), r(5, 5), r(8, 10), r(15, 20), r(24, 24), r(25, 25)),
        result
    );
  }

  @Test
  void withIntervals() {

    final List<Pair<Instant, Instant>> result = IntervalIntersectionUtil.intersectList(
        ImmutableList.of(ri(0, 4), ri(5, 10), ri(13, 20), ri(24, 25)),
        ImmutableList.of(ri(1, 5), ri(8, 12), ri(15, 24), ri(25, 26))
    );

    System.out.println(result);

  }

  @Test
  void failIfNotDisjoint() {
    Assertions.assertThrows(IllegalArgumentException.class, () ->
        IntervalIntersectionUtil.checkRangesListPreconditions(
            ImmutableList.of(r(0, 10), r(5, 20)))
    );
  }

  @Test
  void failIfNotDisjointByOne() {
    Assertions.assertThrows(IllegalArgumentException.class, () ->
        IntervalIntersectionUtil.checkRangesListPreconditions(
            ImmutableList.of(r(0, 100), r(100, 200)))
    );
  }

  @Test
  void failedOrder() {
    Assertions.assertThrows(IllegalArgumentException.class, () ->
        IntervalIntersectionUtil.checkRangesListPreconditions(
            ImmutableList.of(r(50, 100), r(0, 10)))
    );
  }

  @Test()
  void failIfNotDisjointSubset() {
    Assertions.assertThrows(IllegalArgumentException.class, () ->
        IntervalIntersectionUtil.checkRangesListPreconditions(
            ImmutableList.of(r(0, 100), r(10, 90)))
    );
  }

  @Test()
  void failedRange() {
    Assertions.assertThrows(IllegalArgumentException.class, () ->
        IntervalIntersectionUtil.checkRangesListPreconditions(
            ImmutableList.of(r(100, 99)))
    );
  }


  private Pair<Long, Long> r(long a, long b) {
    return Pair.of(a, b);
  }


  private Pair<Instant, Instant> ri(int a, int b) {
    return Pair.of(
        Instant.parse("2007-12-03T10:00:00.00Z").plusSeconds(a),
        Instant.parse("2007-12-03T10:00:00.00Z").plusSeconds(b));
  }


}

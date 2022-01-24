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
        ImmutableList.of(ro(0, 5), ro(5, 11), ro(13, 21), ro(24, 26)),
        ImmutableList.of(ro(1, 6), ro(8, 13), ro(15, 25), ro(25, 27))
    );

    Assertions.assertEquals(
        ImmutableList.of(ro(1, 5), ro(5, 6), ro(8, 11), ro(15, 21), ro(24, 25), ro(25, 26)),
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
            ImmutableList.of(ro(100, 99)))
    );
  }


  /**
   * @param left inclusive
   * @param right inclusive
   */
  private Pair<Long, Long> r(long left, long right) {
    return Pair.of(left, right + 1);
  }

  /**
   * @param left inclusive
   * @param right exclusive
   */
  private Pair<Long, Long> ro(long left, long right) {
    return Pair.of(left, right);
  }



  private Pair<Instant, Instant> ri(int a, int b) {
    return Pair.of(
        Instant.parse("2007-12-03T10:00:00.00Z").plusSeconds(a),
        Instant.parse("2007-12-03T10:00:00.00Z").plusSeconds(b).plusMillis(1));
  }


}

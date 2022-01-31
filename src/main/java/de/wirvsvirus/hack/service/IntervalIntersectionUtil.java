package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;

/**
 * takes two lists of (unsorted intervals) and intersect them
 * <p>
 * https://www.geeksforgeeks.org/find-intersection-of-intervals-given-by-two-lists/
 */
public final class IntervalIntersectionUtil {

  public static final Instant INSTANT_MIN = Instant.ofEpochSecond(0);
  public static final Instant INSTANT_MAX = Instant.parse("2055-12-12T12:12:30.00Z");

  /**
   * truncate to timestamp
   */
  public static List<Pair<Instant, Instant>> truncateListUnit(
      final List<Pair<Instant, Instant>> list,
      final Instant truncateTimestamp) {

    return intersectList(list, Collections.singletonList(
        Pair.of(INSTANT_MIN, truncateTimestamp)));
  }

  public static List<Pair<Instant, Instant>> intersectList(
      final List<Pair<Instant, Instant>> list1,
      final List<Pair<Instant, Instant>> list2) {
    final List<Pair<Long, Long>> epochStyle = IntervalIntersectionUtil.intersectListOfLongs(
        list1.stream()
            .map(p -> Pair.of(p.getLeft().toEpochMilli(), p.getRight().toEpochMilli()))
            .collect(Collectors.toList()),
        list2.stream()
            .map(p -> Pair.of(p.getLeft().toEpochMilli(), p.getRight().toEpochMilli()))
            .collect(Collectors.toList())
    );

    return
        epochStyle.stream()
            .map(
                p -> Pair.of(Instant.ofEpochMilli(p.getLeft()), Instant.ofEpochMilli(p.getRight())))
            .collect(Collectors.toList());
  }

  static List<Pair<Long, Long>> intersectListOfLongs(
      final List<Pair<Long, Long>> list1,
      final List<Pair<Long, Long>> list2) {
    checkRangesListPreconditions(list1);
    checkRangesListPreconditions(list2);

    final List<Pair<Long, Long>> output = new ArrayList<>();

    // i and j pointers for arr1 and
    // arr2 respectively
    int i = 0;
    int j = 0;

    final int n = list1.size();
    final int m = list2.size();

    // Loop through all intervals unless
    // one of the interval gets exhausted
    while (i < n && j < m) {

      // Left bound for intersecting segment
      final long l = Math.max(list1.get(i).getLeft(), list2.get(j).getLeft());

      // Right bound for intersecting segment
      final long r = Math.min(list1.get(i).getRight(), list2.get(j).getRight());

      // If segment is valid print it
      if (l < r) {
        output.add(Pair.of(l, r));
      }

      // If i-th interval's right bound is
      // smaller increment i else increment j
      if (list1.get(i).getRight() < list2.get(j).getRight()) {
        i++;
      } else {
        j++;
      }
    }
    return output;
  }

  static void checkRangesListPreconditions(final List<Pair<Long, Long>> list) {
    StreamEx.of(list)
        .forEach(
            p -> Preconditions.checkArgument(
                p.getLeft() <= p.getRight(),
                "Range %s malformed", p));
    final boolean strictMonotonicAsc = !StreamEx.of(list)
        .pairMap((l, r) -> {
          Preconditions.checkArgument(l.getLeft() < r.getLeft(), "Ranges must be ordered");
          return l.getRight() <= r.getLeft();
        })
        .has(false);
    if (!strictMonotonicAsc) {
      throw new IllegalArgumentException("Ranges must be ordered strict monotonic!");
    }
    Preconditions.checkArgument(strictMonotonicAsc, "Ranges must be ordered strict monotonic!");
  }

  private IntervalIntersectionUtil() {
  }

}

package de.wirvsvirus.hack.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class AlgoUtilTest {
  final int[] fibs = {
    0, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946,
    17711, 28657, 46368, 75025, 121393, 196418, 317811
  };

  @Test
  void newCode() {
      final String code = AlgoUtil.generateGroupCode();
      assertTrue(Integer.parseInt(code) >= 100000);
  }

  @Test
  void testIsFibonacciNumber() {
    int currentFib = fibs[0];
    assertTrue(AlgoUtil.isFibonacciNumber(currentFib));

    for (int i = 1; i < fibs.length; i++) {
      final int nextFib = fibs[i];
      IntStream.range(currentFib + 1, nextFib)
          .forEach(nonFib -> assertFalse(AlgoUtil.isFibonacciNumber(nonFib)));
      assertTrue(AlgoUtil.isFibonacciNumber(nextFib));
      currentFib = nextFib;
    }
  }
}

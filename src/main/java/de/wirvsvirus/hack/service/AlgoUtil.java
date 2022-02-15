package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Random;

public class AlgoUtil {
  private static long fibonacci30[] = {
    0, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946,
    17711, 28657, 46368, 75025, 121393, 196418, 317811, 514229
  };

  /** must not start with 0 */
  public static String generateGroupCode() {
    final int code = new Random().nextInt(1000000 - 100000) + 100000;
    return String.valueOf(code);
  }

  public static boolean isFibonacciNumber(final long numberToTest) {
    Preconditions.checkArgument(
        numberToTest <= fibonacci30[fibonacci30.length - 1],
        String.format(
            "This method is only defined for numbers <= %d, got: %d",
            fibonacci30[fibonacci30.length - 1], numberToTest));
    return Arrays.stream(fibonacci30).anyMatch(fib -> fib == numberToTest);
  }

  private AlgoUtil() {}
}

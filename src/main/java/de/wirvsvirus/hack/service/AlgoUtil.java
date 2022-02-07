package de.wirvsvirus.hack.service;

import java.util.Random;

public class AlgoUtil {

  /** must not start with 0 */
  public static String generateGroupCode() {
    final int code = new Random().nextInt(1000000 - 100000) + 100000;
    return String.valueOf(code);
  }

  /** https://stackoverflow.com/questions/2432669/test-if-a-number-is-fibonacci */
  static boolean isFibonacciNumber(final long numberToTest) {
    final double root5 = Math.sqrt(5);
    final double phi = (1 + root5) / 2;

    final long fibIndex = (long) Math.floor(Math.log(numberToTest * root5) / Math.log(phi) + 0.5);
    final long res = (long) Math.floor(Math.pow(phi, fibIndex) / root5 + 0.5);

    return (res == numberToTest);
  }
}

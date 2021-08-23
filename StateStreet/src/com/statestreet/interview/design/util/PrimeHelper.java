package com.statestreet.interview.design.util;

/**
 * Helper class with a static utility method as it is really a stateless
 * computation.
 **/
public class PrimeHelper {

	public static boolean isPrime(Integer number) {
		if (number == 0 || number == 1) {
			// System.out.println(number + " is not prime");
			return false;
		} else {
			for (int i = 2; i <= number / 2; i++) {
				if (number % i == 0) {
					// System.out.println(number + " is not prime number");
					return false;
				}
			}
		}
		return true;
	}
}
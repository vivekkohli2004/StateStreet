package com.statestreet.interview.design.model;

//Result model depicting the result created at & received from Server.
public class PrimeResult {

	private int number;

	private boolean isPrime;

	public PrimeResult(int number, boolean isPrime) {
		this.number = number;
		this.isPrime = isPrime;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isPrime() {
		return isPrime;
	}

	public void setPrime(boolean isPrime) {
		this.isPrime = isPrime;
	}

}

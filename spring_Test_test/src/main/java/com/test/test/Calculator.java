package com.test.test;

public class Calculator {

	public Calculator() {
		System.out.println("생성됨");
	}

	public long sum(long... numbers) {
		long result = 0L;
		for (long num : numbers) {
			result += num;
		}
		return result;
	}
}

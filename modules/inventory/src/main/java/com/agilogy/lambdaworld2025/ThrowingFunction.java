package com.agilogy.lambdaworld2025;

@FunctionalInterface()
public interface ThrowingFunction<A, R, E extends Throwable> {
  R apply(A argument) throws E;
}

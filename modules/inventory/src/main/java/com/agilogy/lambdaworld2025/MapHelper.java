package com.agilogy.lambdaworld2025;


import java.util.ArrayList;
import java.util.List;

public final class MapHelper {
  private MapHelper() {}

  public <A, R, E extends Exception> List<R> map(Iterable<A> i, ThrowingFunction<A, R, E> f) throws E {
    ArrayList<R> result = new ArrayList<>();
    for (A a : i) {
      result.add(f.apply(a));
    }
    return result;
  }
}

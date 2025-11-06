package org.leralix.tan.upgrade.rewards;

public interface StatDefinition<T extends AggregatableStat<T>> {
  T scale(int level);
}

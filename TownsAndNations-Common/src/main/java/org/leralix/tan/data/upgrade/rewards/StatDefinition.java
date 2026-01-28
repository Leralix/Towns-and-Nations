package org.leralix.tan.data.upgrade.rewards;

public interface StatDefinition<T extends AggregatableStat<T>> {
    T scale(int level);
}

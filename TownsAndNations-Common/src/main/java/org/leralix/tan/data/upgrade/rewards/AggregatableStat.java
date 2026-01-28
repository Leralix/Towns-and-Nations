package org.leralix.tan.data.upgrade.rewards;

import java.util.List;

public interface AggregatableStat<T extends AggregatableStat<T>> {

    T aggregate(List<T> stats);

    T scale(int factor);

}

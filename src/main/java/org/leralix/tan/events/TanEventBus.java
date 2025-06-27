package org.leralix.tan.events;

import java.util.*;

public class TanEventBus {


    private final Map<Class<?>, List<TanEventListener<?>>> listeners = new HashMap<>();

    public <T> void registerListener(Class<T> eventType, TanEventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void callEvent(T event) {
        List<TanEventListener<?>> list = listeners.getOrDefault(event.getClass(), Collections.emptyList());
        for (TanEventListener<?> listener : list) {
            ((TanEventListener<T>) listener).onEvent(event);
        }
    }

}

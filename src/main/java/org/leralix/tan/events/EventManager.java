package org.leralix.tan.events;

import org.leralix.tan.TownsAndNations;
import org.tan.api.events.TanListener;
import org.tan.api.getters.TanEventManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager implements TanEventManager {

    private final Map<Class<?>, List<RegisteredListener>> listeners;

    static EventManager instance;

    private EventManager(){
        this.listeners = new HashMap<>();
    }

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void registerEvents(TanListener listenerInstance) {
        for (Method method : listenerInstance.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(TanListener.EventHandler.class)) continue;

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) continue;

            Class<?> eventType = params[0];

            method.setAccessible(true);
            RegisteredListener registered = new RegisteredListener(listenerInstance, method);

            listeners
                    .computeIfAbsent(eventType, e -> new ArrayList<>())
                    .add(registered);
        }
    }

    public <T> void callEvent(T event) {
        List<RegisteredListener> list = listeners.getOrDefault(event.getClass(), List.of());

        for (RegisteredListener reg : list) {
            reg.invoke(event);
        }
    }

    private record RegisteredListener(Object instance, Method method) {

        public void invoke(Object event) {
                try {
                    method.invoke(instance, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    TownsAndNations.getPlugin().getLogger().warning("Failed to invoke event handler: " + e.getMessage());
                }
            }
        }


}

package org.leralix.tan.data;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DataWrapperFactory {

    public static <T> T wrap(T data, Runnable save, Class<T> iface) {
        Object proxy = Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class[]{iface},
                (p, method, args) -> {
                    Object result = method.invoke(data, args);

                    if (isMutator(method)) {
                        save.run();
                    }

                    return result;
                }
        );

        return iface.cast(proxy);
    }

    private static boolean isMutator(Method method) {
        String name = method.getName();
        return name.startsWith("set")
                || name.startsWith("add")
                || name.startsWith("remove")
                || name.startsWith("clean")
                || name.startsWith("clear");
    }
}
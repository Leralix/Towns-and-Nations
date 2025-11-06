package org.leralix.tan.events;

public abstract class TanEventListener<T> {

  public abstract void onEvent(T event);
}

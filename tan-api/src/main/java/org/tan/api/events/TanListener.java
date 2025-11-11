package org.tan.api.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface for TAN event listeners. Classes implementing this interface can register custom
 * event handlers using the @EventHandler annotation.
 */
public interface TanListener {

  /** Annotation to mark methods as event handlers for TAN events. */
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  @interface EventHandler {}
}

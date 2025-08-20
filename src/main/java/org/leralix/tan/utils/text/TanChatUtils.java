package org.leralix.tan.utils.text;

import org.leralix.tan.lang.Lang;

/**
 * This class is used for chat related utilities.
 */
public class TanChatUtils {

    private TanChatUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * This method send the prefix for every chat message in the plugin.
     * @return the prefix for every chat message in the plugin.
     */
    public static String getTANString(){
        return Lang.PLUGIN_STRING.get();
    }

    /**
     * This method send the prefix for every debug or admin chat message in the plugin.
     * @return the prefix for every debug or admin chat message in the plugin.
     */
    public static String getTANDebugString(){
        return Lang.PLUGIN_DEBUG_STRING.get();
    }

}

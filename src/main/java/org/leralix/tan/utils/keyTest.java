package org.leralix.tan.utils;

import org.leralix.lib.utils.config.ConfigTag;

public enum keyTest extends ConfigTag {

    private final String key;

    keyTest(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}

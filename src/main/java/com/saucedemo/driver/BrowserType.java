package com.saucedemo.driver;

import java.util.Arrays;

/** Supported browsers, resolved from configuration in a case-insensitive way. */
public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType from(String value) {
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported browser '" + value + "'. Supported: " + Arrays.toString(values())));
    }
}

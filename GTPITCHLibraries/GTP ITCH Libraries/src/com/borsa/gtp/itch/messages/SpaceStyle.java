package com.borsa.gtp.itch.messages;

public enum SpaceStyle {
    spaceStyle1("some-key"),
    spaceStyle2("some-other-key"),
    spaceStyle3("foo-bar");

    private String key;

    private SpaceStyle(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
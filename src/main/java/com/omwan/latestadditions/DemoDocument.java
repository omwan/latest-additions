package com.omwan.latestadditions;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DemoDocument {

    private String key;

    public DemoDocument() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

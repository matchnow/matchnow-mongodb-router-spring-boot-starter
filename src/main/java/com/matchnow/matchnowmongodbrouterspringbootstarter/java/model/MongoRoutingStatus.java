package com.matchnow.matchnowmongodbrouterspringbootstarter.java.model;

public enum MongoRoutingStatus {
    READ, WRITE;

    private boolean isDefault;

    public boolean isDefault() {
        return isDefault;
    }

    void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public MongoRoutingStatus defaultStatus() {
        return READ.isDefault ? READ : WRITE;
    }
}

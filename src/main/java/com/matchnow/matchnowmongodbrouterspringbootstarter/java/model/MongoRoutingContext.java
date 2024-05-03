package com.matchnow.matchnowmongodbrouterspringbootstarter.java.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;

import static com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingStatus.READ;
import static com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingStatus.WRITE;

@Slf4j
public class MongoRoutingContext {
    private static final ThreadLocal<MongoRoutingStatus> currentStatus = new NamedThreadLocal<>("Mongo routing status");

    public static MongoRoutingStatus getCurrentStatus() {
        return currentStatus.get() == null ? READ : currentStatus.get();
    }

    public static void setCurrentStatus(MongoRoutingStatus status) {
        if (currentStatus.get() == null) {
            log.debug("MongoRoutingStatus updated. MongoRoutingStatus -> " + status);
            currentStatus.set(status);
            return;
        }
        if (currentStatus.get().equals(READ) && status.equals(WRITE)) {
            throw new IllegalArgumentException("MongoRoutingStatus.WRITE attempt in MongoRoutingStatus.READ");
        }
        if (currentStatus.get().equals(WRITE) && status.equals(READ)) {
            log.warn("MongoRoutingStatus.READ attempt in MongoRoutingStatus.WRITE");
        }
    }

    public static void reset() {
        log.debug("MongoRoutingContext reset. MongoRoutingStatus -> null");
        currentStatus.remove();
    }
}

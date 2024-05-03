package com.matchnow.matchnowmongodbrouterspringbootstarter.java.aop;

import com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingStatus.READ;
import static com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingStatus.WRITE;

@Aspect
@Component
public class MongoRoutingAdvice {
    @Before(
            "@annotation(org.springframework.transaction.annotation.Transactional) || " +
                    "@annotation(com.matchnow.matchnowmongodbrouterspringbootstarter.java.annotations.MongoWrite)"
    )
    public void beforeWrite() {
        MongoRoutingContext.setCurrentStatus(WRITE);
    }

    @Before("@annotation(com.matchnow.matchnowmongodbrouterspringbootstarter.java.annotations.MongoRead)")
    public void beforeRead() {
        MongoRoutingContext.setCurrentStatus(READ);
    }
}

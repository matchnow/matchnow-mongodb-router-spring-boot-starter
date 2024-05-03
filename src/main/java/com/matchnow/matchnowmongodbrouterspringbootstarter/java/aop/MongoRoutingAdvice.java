package com.matchnow.matchnowmongodbrouterspringbootstarter.java.aop;

import com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingStatus.READ;
import static com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingStatus.WRITE;

@Order(2)
@Aspect
@Component
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enable-routing", havingValue = "true")
public class MongoRoutingAdvice {
    @Before(
            "@annotation(org.springframework.transaction.annotation.Transactional) || " +
                    "@annotation(com.matchnow.matchnowmongodbrouter.annotations.MongoWrite)"
    )
    public void beforeWrite() {
        MongoRoutingContext.setCurrentStatus(WRITE);
    }

    @Before("@annotation(com.matchnow.matchnowmongodbrouter.annotations.MongoRead)")
    public void beforeRead() {
        MongoRoutingContext.setCurrentStatus(READ);
    }
}

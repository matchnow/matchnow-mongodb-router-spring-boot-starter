package com.matchnow.matchnowmongodbrouter.java.aop;

import com.matchnow.matchnowmongodbrouter.java.model.MongoRoutingContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import static com.matchnow.matchnowmongodbrouter.java.model.MongoRoutingStatus.READ;
import static com.matchnow.matchnowmongodbrouter.java.model.MongoRoutingStatus.WRITE;

@Aspect
@Component
public class MongoRoutingAdvice implements Ordered {
    @Before(
            "@annotation(org.springframework.transaction.annotation.Transactional) || " +
                    "@annotation(com.matchnow.matchnowmongodbrouter.java.annotations.MongoWrite)"
    )
    public void beforeWrite() {
        MongoRoutingContext.setCurrentStatus(WRITE);
    }

    @Before("@annotation(com.matchnow.matchnowmongodbrouter.java.annotations.MongoRead)")
    public void beforeRead() {
        MongoRoutingContext.setCurrentStatus(READ);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}

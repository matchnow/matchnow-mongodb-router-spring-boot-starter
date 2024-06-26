package com.matchnow.matchnowmongodbrouterspringbootstarter.java.aop;

import com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Aspect
@Component
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enable-routing", havingValue = "true")
public class MongoRoutingResetAdvice {
    // TODO: 동적으로 포인트컷 설정할 수 있도록 변경
    @Before(
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
                    "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
                    "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
                    "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
                    "@annotation(org.springframework.web.bind.annotation.DeleteMapping)"
    )
    public void beforeController() {
        MongoRoutingContext.reset();
    }

    @Before("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void beforeSchedule() {
        MongoRoutingContext.reset();
    }

    @Before("@annotation(org.springframework.scheduling.annotation.Async)")
    public void beforeAsync() {
        MongoRoutingContext.reset();
    }

    @Before("@annotation(com.matchnow.matchnowmongodbrouter.annotations.MongoReset)")
    public void onAnn() {
        MongoRoutingContext.reset();
    }
}

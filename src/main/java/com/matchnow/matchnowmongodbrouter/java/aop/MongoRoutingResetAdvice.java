package com.matchnow.matchnowmongodbrouter.java.aop;

import com.matchnow.matchnowmongodbrouter.java.model.MongoRoutingContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MongoRoutingResetAdvice implements Ordered {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerBeans() {
    }

    @Pointcut("within(@org.springframework.scheduling.annotation.Async *)")
    public void asyncBeans() {
    }

    @Pointcut("execution(* *(..))")
    public void allMethods() {
    }

    @Before("controllerBeans() && allMethods()")
    public void beforeController() {
        MongoRoutingContext.reset();
    }

    @Before("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void beforeSchedule() {
        MongoRoutingContext.reset();
    }

    @Before("asyncBeans() && allMethods()")
    public void beforeAsyncBeans() {
        MongoRoutingContext.reset();
    }

    @Before("@annotation(org.springframework.scheduling.annotation.Async)")
    public void beforeAsync() {
        MongoRoutingContext.reset();
    }

    @Before("@annotation(com.matchnow.matchnowmongodbrouter.java.annotations.MongoReset)")
    public void onAnn() {
        MongoRoutingContext.reset();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

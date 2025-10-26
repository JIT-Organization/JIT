package com.justintime.jit.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.justintime.jit.controller..*(..)) || " +
            "execution(* com.justintime.jit.service.impl..*(..)) || " +
            "execution(* com.justintime.jit.util..*(..))")
    public void applicationPackagePointcut() {}

    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info(">> Request to: {}", joinPoint.getSignature());
    }

    @AfterReturning(pointcut = "applicationPackagePointcut()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        logger.info(">> Response from: {} -> {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error(">> Exception in {}: {}", joinPoint.getSignature(), ex.getMessage(), ex);
    }
}

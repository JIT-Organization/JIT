package com.justintime.jit.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.justintime.jit.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Request to: {}", joinPoint.getSignature());
    }

    @AfterReturning(pointcut = "execution(* com.example.demo.controller..*(..))", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        logger.info("Response from: {} -> {}", joinPoint.getSignature(), result);
    }
}

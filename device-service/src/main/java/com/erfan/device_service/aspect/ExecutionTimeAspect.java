package com.erfan.device_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    @Pointcut("execution(* com.erfan.device_service.controller.*.*(..))")
    public void controllerMethod() {}

    @Around("controllerMethod()")
    public Object measureExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start=System.nanoTime();
        try{
            return joinPoint.proceed();
        }finally {
            long end=System.nanoTime();
            long elapsedNs = end-start;
            long elapsedMs= TimeUnit.NANOSECONDS.toMillis(elapsedNs);
            String signature= joinPoint.getSignature().toShortString();
            log.info("Controller method {} Execution in {} ms",signature, elapsedMs);
        }
    }
}

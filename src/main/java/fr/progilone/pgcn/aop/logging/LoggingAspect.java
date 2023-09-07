package fr.progilone.pgcn.aop.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect for logging execution of service and repository Spring components.
 *
 * By default, it only runs with the "dev" profile.
 */
@Aspect
public class LoggingAspect {

    private final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);

    private final long warnDuration;

    public LoggingAspect(Long warnDuration) {
        this.warnDuration = warnDuration != null ? warnDuration
                                                 : 1500l;
    }

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" + " || within(@org.springframework.stereotype.Service *)"
              + " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(fr.progilone.pgcn.repository..*)" + " || within(fr.progilone.pgcn.service..*)"
              + " || within(fr.progilone.pgcn.web.rest..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Pointcut("execution(public !void org.springframework.data.repository.Repository+.*(..))")
    public void publicNonVoidRepositoryMethod() {
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint
     *            join point for advice
     * @return result
     * @throws Throwable
     *             throws IllegalArgumentException
     */
    @Around("(applicationPackagePointcut() && springBeanPointcut()) || publicNonVoidRepositoryMethod()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedTime = System.currentTimeMillis() - start;
            if (elapsedTime > warnDuration) {
                LOG.warn("Execution time : {} ms for method: {}.{}()", elapsedTime, joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            } else if (LOG.isTraceEnabled()) {
                LOG.trace("Execution time : {} ms for method: {}.{}()", elapsedTime, joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            }
        }
    }
}

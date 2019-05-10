package fr.progilone.pgcn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import fr.progilone.pgcn.aop.logging.LoggingAspect;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {
    
    @Value("${logging.warnDuration}")
    private Long warnDuration;
 
    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect(warnDuration);
    }

}

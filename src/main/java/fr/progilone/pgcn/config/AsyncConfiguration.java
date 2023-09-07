package fr.progilone.pgcn.config;

import fr.progilone.pgcn.async.ExceptionHandlingAsyncTaskExecutor;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration implements AsyncConfigurer, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncConfiguration.class);

    private Environment environment;

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    @Override
    @Bean(name = "taskExecutor")
    // On utilise un DelegatingSecurityContextAsyncTaskExecutor pour que le contexte SpringSecurity soit propagé aux threads enfants.
    // On l'instancie comme ceci car sinon le threadPool délégué n'est pas initialisé ni fermé car le DelegatingSecurityContextAsyncTaskExecutor
    // n'implémente pas InitializingBean ni DisposableBean
    public Executor getAsyncExecutor() {
        return new DelegatingSecurityContextAsyncTaskExecutor(threadPoolTaskExecutor());
    }

    @Bean
    public AsyncTaskExecutor threadPoolTaskExecutor() {
        LOG.debug("Creating Async Task Executor");
        final ThreadPoolTaskExecutor executor = createThreadPoolTaskExecutor(environment.getProperty("async.corePoolSize",
                                                                                                     Integer.class,
                                                                                                     Runtime.getRuntime().availableProcessors() / 2),
                                                                             environment.getProperty("async.maxPoolSize", Integer.class, 50),
                                                                             environment.getProperty("async.queueCapacity", Integer.class, Integer.MAX_VALUE),
                                                                             "Numahop-executor-");
        return new ExceptionHandlingAsyncTaskExecutor(executor);
    }

    @Bean("jobRunnerExecutor")
    public ThreadPoolTaskExecutor jobRunnerExecutor() {
        return createThreadPoolTaskExecutor(Runtime.getRuntime().availableProcessors(), Integer.MAX_VALUE, 0, "jobrunner-");
    }

    private ThreadPoolTaskExecutor createThreadPoolTaskExecutor(final int corePoolSize, final int maxPoolSize, final int queueCapacity, final String namePrefix) {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(namePrefix);
        // On crée un décorateur qui va recopier le SecurityContext du thread appelant vers le thread exécutant car sinon on se retrouve
        // avec les informations de la personne précédente car les threads sont dans un pool et ne sont pas recréés à chaque utilisation.
        executor.setTaskDecorator(r -> {
            final SecurityContext securityContext = SecurityContextHolder.getContext();
            return () -> {
                try {
                    if (securityContext != null) {
                        SecurityContextHolder.setContext(securityContext);
                    }
                    r.run();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            };
        });
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}

package fr.progilone.pgcn.async;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;

public class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor, InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlingAsyncTaskExecutor.class);

    private final AsyncTaskExecutor executor;

    public ExceptionHandlingAsyncTaskExecutor(final AsyncTaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(final Runnable task) {
        executor.execute(task);
    }

    @Override
    public void execute(final Runnable task, final long startTimeout) {
        executor.execute(createWrappedRunnable(task), startTimeout);
    }

    private <T> Callable<T> createCallable(final Callable<T> task) {
        return () -> {
            try {
                return task.call();
            } catch (final Exception e) {
                handle(e);
                throw e;
            }
        };
    }

    private Runnable createWrappedRunnable(final Runnable task) {
        return () -> {
            try {
                task.run(); // NOSONAR : on appelle directement la méthode run() car c'est un wrapper, c'est donc normal
            } catch (final Exception e) {
                handle(e);
            }
        };
    }

    protected void handle(final Exception e) {
        LOG.error("Caught async exception", e);
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return executor.submit(createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return executor.submit(createCallable(task));
    }

    @Override
    public void destroy() throws Exception {
        if (executor instanceof DisposableBean) {
            final DisposableBean bean = (DisposableBean) executor;
            bean.destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (executor instanceof InitializingBean) {
            final InitializingBean bean = (InitializingBean) executor;
            bean.afterPropertiesSet();
        }
    }
}

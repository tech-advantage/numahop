package fr.progilone.pgcn.service.helper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.util.transaction.TransactionalJobRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by Sebastien on 05/08/2016.
 */
@ExtendWith(MockitoExtension.class)
public class TransactionalJobRunnerTest {

    @Mock
    private TransactionService transactionService;

    @Test
    public void test() {
        final String FAILURE = "_FAIL_";

        final ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
        e.initialize();
        when(transactionService.getTaskExecutor()).thenReturn(e);

        final List<String> checkJob = new ArrayList<>();
        final List<Long> checkProgress = new ArrayList<>();
        final List<String> sample = Arrays.asList("bulldozer",
                                                  "cuisiné",
                                                  "désinstrumentaliser",
                                                  "fourneaux",
                                                  FAILURE,
                                                  "lombric",
                                                  "neutrodyner",
                                                  "recraqueler",
                                                  "s'entremordre",
                                                  "étourneaux");

        new TransactionalJobRunner<>(sample, transactionService).setCommit(2)
                                                                .forEach(s -> !StringUtils.equals(s, FAILURE) && checkJob.add(s))
                                                                .onProgress(checkProgress::add)
                                                                .process();

        assertEquals(sample.size() - 1, checkJob.size());
        assertArrayEquals(new String[] {"bulldozer",
                                        "cuisiné",
                                        "désinstrumentaliser",
                                        "fourneaux",
                                        "lombric",
                                        "neutrodyner",
                                        "recraqueler",
                                        "s'entremordre",
                                        "étourneaux"}, checkJob.toArray(new String[] {}));
        assertArrayEquals(new Long[] {0L,
                                      2L,
                                      4L,
                                      6L,
                                      8L,
                                      9L}, checkProgress.toArray(new Long[] {}));

        verify(transactionService, times(5)).startTransaction(false);
        verify(transactionService, times(5)).commitTransaction(isNull());
    }
}

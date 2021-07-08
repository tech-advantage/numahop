package fr.progilone.pgcn.service.helper;

import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.util.transaction.TransactionalJobRunner;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Sebastien on 05/08/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionalJobRunnerTest {

    @Mock
    private TransactionService transactionService;

    @Test
    public void test() {
        final String FAILURE = "_FAIL_";

        final List<String> checkJob = new ArrayList<>();
        final List<Long> checkProgress = new ArrayList<>();
        List<String> sample = Arrays.asList("bulldozer",
                                            "cuisiné",
                                            "désinstrumentaliser",
                                            "fourneaux",
                                            FAILURE,
                                            "lombric",
                                            "neutrodyner",
                                            "recraqueler",
                                            "s'entremordre",
                                            "étourneaux");

        new TransactionalJobRunner<String>(transactionService)
            .setCommit(2)
            .setMaxThreads(1)
            .forEach(s -> !StringUtils.equals(s, FAILURE) && checkJob.add(s))
            .onProgress(checkProgress::add)
            .process(sample.iterator());

        Assert.assertEquals(sample.size() - 1, checkJob.size());
        Assert.assertArrayEquals(new String[] {"bulldozer",
                                               "cuisiné",
                                               "désinstrumentaliser",
                                               "fourneaux",
                                               "lombric",
                                               "neutrodyner",
                                               "recraqueler",
                                               "s'entremordre",
                                               "étourneaux"},
                                 checkJob.toArray(new String[] {}));
        Assert.assertArrayEquals(new Long[] {0L, 2L, 4L, 6L, 8L, 9L}, checkProgress.toArray(new Long[] {}));

        verify(transactionService, times(5)).startTransaction(false);
        verify(transactionService, times(5)).commitTransaction(any(TransactionStatus.class));
    }
}

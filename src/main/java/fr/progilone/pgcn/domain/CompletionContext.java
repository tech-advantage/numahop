package fr.progilone.pgcn.domain;

import org.springframework.data.elasticsearch.core.completion.Completion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompletionContext extends Completion {

    private final Map<String, List<String>> context = new HashMap<>();

    private CompletionContext() {
        super(null);
    }

    public CompletionContext(final String[] input) {
        super(input);
    }

    public Map<String, List<String>> getContext() {
        return context;
    }

    public void addContext(final String contextName, final String value) {
        this.context.computeIfAbsent(contextName, k -> new ArrayList<>()).add(value);
    }
}

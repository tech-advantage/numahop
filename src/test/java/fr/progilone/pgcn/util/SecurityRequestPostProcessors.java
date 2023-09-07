package fr.progilone.pgcn.util;

import java.util.stream.Stream;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class SecurityRequestPostProcessors {

    public static RequestPostProcessor roles(final String... roles) {
        return postProcessor -> {
            Stream.of(roles).forEach(postProcessor::addUserRole);
            return postProcessor;
        };
    }
}

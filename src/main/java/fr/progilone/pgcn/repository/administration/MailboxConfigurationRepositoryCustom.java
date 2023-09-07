package fr.progilone.pgcn.repository.administration;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import java.util.List;

public interface MailboxConfigurationRepositoryCustom {

    List<MailboxConfiguration> search(String search, List<String> libraries, boolean active);
}

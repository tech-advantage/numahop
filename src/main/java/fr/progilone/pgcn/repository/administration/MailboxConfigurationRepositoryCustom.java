package fr.progilone.pgcn.repository.administration;

import java.util.List;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;

public interface MailboxConfigurationRepositoryCustom {

    List<MailboxConfiguration> search(String search, List<String> libraries, boolean active);
}

package fr.progilone.pgcn.repository.project;

import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.repository.project.helper.ProjectSearchBuilder;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {

    /**
     * Recherche des projets
     *
     * @param searchBuilder
     * @param pageable
     * @return
     */
    Page<Project> search(ProjectSearchBuilder searchBuilder, Pageable pageable);

    List<Project> findProjectsForWidget(LocalDate fromDate, List<String> libraries, List<Project.ProjectStatus> statuses);
}

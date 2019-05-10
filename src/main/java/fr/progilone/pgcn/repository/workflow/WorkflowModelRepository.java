package fr.progilone.pgcn.repository.workflow;

import java.util.List;

import fr.progilone.pgcn.domain.library.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.workflow.WorkflowModel;

public interface WorkflowModelRepository extends JpaRepository<WorkflowModel, String>, WorkflowModelRepositoryCustom {

    Long countByName(String name);

    @Query("select distinct model " +
            "from WorkflowModel model " +
            "left join model.library lib " +
            "where lib.identifier = ?1 and model.active = ?2")
    List<WorkflowModel> findAllByLibraryIdentifierAndActive(String identifier, boolean active);

    Long countByNameAndIdentifierNot(String name, String identifier);

    Long countByLibrary(Library library);
}

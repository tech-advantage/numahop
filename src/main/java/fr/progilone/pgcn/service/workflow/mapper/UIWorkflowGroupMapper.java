package fr.progilone.pgcn.service.workflow.mapper;

import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.dto.workflow.WorkflowGroupDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.user.UserService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UIWorkflowGroupMapper {

    private final UserService userService;
    private final LibraryService libraryService;

    @Autowired
    public UIWorkflowGroupMapper(final UserService userService, final LibraryService libraryService) {
        this.userService = userService;
        this.libraryService = libraryService;
    }

    public void mapInto(final WorkflowGroupDTO dto, final WorkflowGroup domainObject) {
        domainObject.setIdentifier(dto.getIdentifier());
        domainObject.setName(dto.getName());
        domainObject.setDescription(dto.getDescription());

        // Users
        List<SimpleUserDTO> users = dto.getUsers();
        if (users != null) {
            domainObject.setUsers(users.stream().map(user -> userService.getOne(user.getIdentifier())).collect(Collectors.toSet()));
        } else {
            domainObject.setUsers(null);
        }

        // Bibliothèque
        Library library = null;
        if (dto.getLibrary() != null) {
            library = libraryService.findByIdentifier(dto.getLibrary().getIdentifier());
        } else {
            if (SecurityUtils.getCurrentUser().getLibraryId() != null) {
                library = libraryService.findByIdentifier(SecurityUtils.getCurrentUser().getLibraryId());
            }
        }
        domainObject.setLibrary(library);

    }
}

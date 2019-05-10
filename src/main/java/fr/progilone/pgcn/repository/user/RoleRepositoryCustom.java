package fr.progilone.pgcn.repository.user;

import fr.progilone.pgcn.domain.user.Role;

import java.util.List;

public interface RoleRepositoryCustom {

    List<Role> search(String search,
                      final List<String> authorizations);

}

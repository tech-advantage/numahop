package fr.progilone.pgcn.repository.library;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.progilone.pgcn.domain.library.LibraryParameterValueCines;

public interface LibraryParameterCinesRepository extends JpaRepository<LibraryParameterValueCines, String> {
    
}

package fr.progilone.pgcn.repository.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.storage.StoredFile;

@Service
public interface StoredFileRepository extends JpaRepository<StoredFile, String> {

    
}

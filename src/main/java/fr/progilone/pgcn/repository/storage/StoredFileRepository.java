package fr.progilone.pgcn.repository.storage;

import fr.progilone.pgcn.domain.storage.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface StoredFileRepository extends JpaRepository<StoredFile, String> {

}

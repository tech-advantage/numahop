package fr.progilone.pgcn.repository.imagemetadata;

import fr.progilone.pgcn.domain.imagemetadata.ImageMetadataProperty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageMetadataRepository extends JpaRepository<ImageMetadataProperty, String> {

    ImageMetadataProperty findOneImageMetadataPropertyByIdentifier(String label);
}

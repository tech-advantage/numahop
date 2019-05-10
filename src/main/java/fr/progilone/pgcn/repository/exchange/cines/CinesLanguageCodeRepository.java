package fr.progilone.pgcn.repository.exchange.cines;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.progilone.pgcn.domain.exchange.cines.CinesLanguageCode;


public interface CinesLanguageCodeRepository  extends JpaRepository<CinesLanguageCode, String> {

    List<CinesLanguageCode> findByOrderByIdentifier();
    
    CinesLanguageCode findOneByIdentifier(String identifier);
    
}

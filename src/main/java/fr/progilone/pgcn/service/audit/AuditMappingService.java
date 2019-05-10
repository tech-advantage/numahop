package fr.progilone.pgcn.service.audit;

import fr.progilone.pgcn.domain.audit.AuditRevision;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.repository.audit.AuditMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Sébastien on 27/06/2017.
 */
@Service
public class AuditMappingService {

    private final AuditMappingRepository auditMappingRepository;

    @Autowired
    public AuditMappingService(AuditMappingRepository auditMappingRepository) {
        this.auditMappingRepository = auditMappingRepository;
    }

    /**
     * Récupère dans l'historique le mapping correspondant à l'identifiant id et à la révision rev
     *
     * @param id
     * @param rev
     * @return
     */
    @Transactional(readOnly = true)
    public Mapping getEntity(final String id, final int rev) {
        return auditMappingRepository.getEntity(id, rev);
    }

    /**
     * Liste les révisions du mapping (et de ses règles) correspondant à l'identifiant id
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<AuditRevision> getRevisions(final String id) {
        return auditMappingRepository.getRevisions(id);
    }
}

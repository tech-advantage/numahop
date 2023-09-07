package fr.progilone.pgcn.service.document;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Sébastien on 30/06/2017.
 */
@Service
public class DocUnitValidationService {

    public final DocUnitRepository docUnitRepository;

    @Autowired
    public DocUnitValidationService(final DocUnitRepository docUnitRepository) {
        this.docUnitRepository = docUnitRepository;
    }

    /**
     * Validation de l'unité documentaire, dans une nouvelle transaction
     *
     * @param doc
     * @return
     * @throws PgcnValidationException
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void validate(final DocUnit doc) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();
        final String pgcnId = doc.getPgcnId();

        // Le libellé est obligatoire
        if (StringUtils.isBlank(doc.getLabel())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DOC_UNIT_LABEL_MANDATORY).setField("label").build());
        }
        // pgcnId est obligatoire
        if (StringUtils.isBlank(pgcnId)) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DOC_UNIT_PGCN_ID_MANDATORY).setField("pgcnId").build());
        }
        // statut + pgcn_id unique
        else {
            final Long countDuplicates = doc.getIdentifier() == null ? docUnitRepository.countByPgcnIdAndState(doc.getPgcnId(), doc.getState())
                                                                     : docUnitRepository.countByPgcnIdAndStateAndIdentifierNot(doc.getPgcnId(),
                                                                                                                               doc.getState(),
                                                                                                                               doc.getIdentifier());
            if (countDuplicates > 0) {
                errors.add(builder.reinit()
                                  .setCode(PgcnErrorCode.DOC_UNIT_DUPLICATE_PGCN_ID)
                                  .setField("pgcnId")
                                  .addComplement("PgcnId: " + pgcnId)
                                  .addComplement("Statut: " + doc.getState())
                                  .build());
            }
        }
        // Les droits sont obligatoires
        if (doc.getRights() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DOC_UNIT_RIGHT_MANDATORY).setField("rights").build());
        }
        // La bibliothèque est obligatoire
        if (doc.getLibrary() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DOC_UNIT_LIBRARY_MANDATORY).setField("library").build());
        }

        /* Retour **/
        if (!errors.isEmpty()) {
            doc.setErrors(errors);
            throw new PgcnValidationException(doc, errors);
        }
    }
}

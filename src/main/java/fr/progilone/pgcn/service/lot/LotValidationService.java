package fr.progilone.pgcn.service.lot;

import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.lot.LotRepository;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de validation des lots
 */
@Service
public class LotValidationService {

    private final LotRepository lotRepository;

    @Autowired
    public LotValidationService(final LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public PgcnList<PgcnError> validate(final Lot lot) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();

        final PgcnError.Builder builder = new PgcnError.Builder();

        // Le label est unique par projet
        if (StringUtils.isNotBlank(lot.getLabel()) && lot.getProject() != null) {
            final List<Lot> duplicates = lotRepository.findByLabelAndProject(lot.getLabel(), lot.getProject());

            if (duplicates.stream().anyMatch(dup -> !Objects.equals(dup, lot))) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.LOT_DUPLICATE_LABEL).setField("label").build());
            }
        }

        // Retour
        if (!errors.isEmpty()) {
            lot.setErrors(errors);
            throw new PgcnValidationException(lot, errors);
        }
        return errors;
    }
}

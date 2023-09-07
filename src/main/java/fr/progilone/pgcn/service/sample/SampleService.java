package fr.progilone.pgcn.service.sample;

import fr.progilone.pgcn.domain.document.sample.Sample;
import fr.progilone.pgcn.domain.dto.sample.SampleDTO;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.sample.SampleRepository;
import fr.progilone.pgcn.service.sample.mapper.SampleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SampleService {

    private final SampleRepository sampleRepository;

    @Autowired
    public SampleService(final SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    @Transactional
    public Sample save(final Sample sample) throws PgcnValidationException, PgcnBusinessException {
        final Sample savedSample = sampleRepository.save(sample);
        return sampleRepository.getOne(savedSample.getIdentifier());
    }

    @Transactional
    public void delete(final String identifier) {
        sampleRepository.deleteById(identifier);
    }

    @Transactional(readOnly = true)
    public Sample getOneWithDep(final String id) {
        return sampleRepository.getSampleWithDep(id);
    }

    @Transactional(readOnly = true)
    public SampleDTO getOne(final String id) {
        final Sample sample = sampleRepository.getOne(id);
        return SampleMapper.INSTANCE.sampleToSampleDTO(sample);
    }

    @Transactional(readOnly = true)
    public SampleDTO findByDelivery(final String deliveryId) {
        final Sample sample = sampleRepository.findByDeliveryIdentifier(deliveryId);
        return SampleMapper.INSTANCE.sampleToSampleDTO(sample);
    }

}

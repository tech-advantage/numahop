package fr.progilone.pgcn.service.library.ui;

import fr.progilone.pgcn.domain.dto.library.LibraryParameterDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryParameterValueCinesDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryParameterValuedDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.library.LibraryParameter;
import fr.progilone.pgcn.domain.library.LibraryParameterValueCines;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.service.library.LibraryParameterService;
import fr.progilone.pgcn.service.library.mapper.LibraryParameterMapper;
import fr.progilone.pgcn.service.library.mapper.UILibraryParameterMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service dédié à les gestion des vues des bibliothèques
 */
@Service
public class UILibraryParameterService {

    private final LibraryParameterService libraryParameterService;
    private final UILibraryParameterMapper uiLibraryParameterMapper;

    @Autowired
    public UILibraryParameterService(final LibraryParameterService libraryParameterService, final UILibraryParameterMapper uiLibraryParameterMapper) {
        this.libraryParameterService = libraryParameterService;
        this.uiLibraryParameterMapper = uiLibraryParameterMapper;

    }

    /**
     * Création d'un paramétrage d'une bibliothèque. Le type de paramétrage est géré par le mapper.
     *
     * @param request
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public LibraryParameterDTO create(final LibraryParameterDTO request) throws PgcnValidationException {
        final LibraryParameter libraryParameter = new LibraryParameter();
        uiLibraryParameterMapper.mapInto(request, libraryParameter);
        try {
            return saveAndReturn(libraryParameter);
        } catch (final PgcnBusinessException e) {
            e.getErrors().forEach(semanthequeError -> request.addError(buildError(semanthequeError.getCode())));
            throw new PgcnValidationException(request);
        }
    }

    /**
     * Création d'un paramétrage valorisé d'une bibliothèque.
     *
     * @param request
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public LibraryParameterValuedDTO create(final LibraryParameterValuedDTO request) throws PgcnValidationException {
        final LibraryParameter libraryParameter = new LibraryParameter();
        uiLibraryParameterMapper.mapValuedInto(request, libraryParameter);
        try {
            return saveAndReturnValued(libraryParameter);
        } catch (final PgcnBusinessException e) {
            e.getErrors().forEach(semanthequeError -> request.addError(buildError(semanthequeError.getCode())));
            throw new PgcnValidationException(request);
        }
    }

    /**
     * Mise à jour d'un paramétrage d'une bibliothèque.
     *
     * @param
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public LibraryParameterDTO update(final LibraryParameterDTO request) throws PgcnValidationException {
        final LibraryParameter libraryParameter = libraryParameterService.findLibraryParameterWithDependencies(request.getIdentifier());
        uiLibraryParameterMapper.mapInto(request, libraryParameter);
        try {
            return saveAndReturn(libraryParameter);
        } catch (final PgcnBusinessException e) {
            e.getErrors().forEach(semanthequeError -> request.addError(buildError(semanthequeError.getCode())));
            throw new PgcnValidationException(request);
        }
    }

    /**
     * Mise à jour d'un paramétrage valorise d'une bibliothèque.
     *
     * @param request
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public LibraryParameterValuedDTO update(final LibraryParameterValuedDTO request) throws PgcnValidationException {
        final LibraryParameter libraryParameter = libraryParameterService.findLibraryParameterWithDependencies(request.getIdentifier());
        uiLibraryParameterMapper.mapValuedInto(request, libraryParameter);
        try {
            return saveAndReturnValued(libraryParameter);
        } catch (final PgcnBusinessException e) {
            e.getErrors().forEach(semanthequeError -> request.addError(buildError(semanthequeError.getCode())));
            throw new PgcnValidationException(request);
        }
    }

    private LibraryParameterDTO saveAndReturn(final LibraryParameter param) {
        final LibraryParameter savedLibraryParameter = libraryParameterService.save(param);
        final LibraryParameter libParamWithValues = libraryParameterService.findLibraryParameterWithDependencies(savedLibraryParameter.getIdentifier());
        return LibraryParameterMapper.INSTANCE.libraryParameterToLibraryParameterDTO(libParamWithValues);
    }

    private LibraryParameterValuedDTO saveAndReturnValued(final LibraryParameter param) {
        final LibraryParameter savedLibraryParameter = libraryParameterService.save(param);
        final LibraryParameter libParamWithValues = libraryParameterService.findCinesDefaultValuesForLibrary(savedLibraryParameter.getLibrary());
        final LibraryParameterValuedDTO dto = LibraryParameterMapper.INSTANCE.libParamToLibParamValuedDTO(libParamWithValues);
        final List<LibraryParameterValueCinesDTO> valuesDto = new ArrayList<>();
        libParamWithValues.getValues().stream().map(LibraryParameterValueCines.class::cast).forEach(val -> {
            valuesDto.add(LibraryParameterMapper.INSTANCE.libParamCinesValueToDTO(val));
        });
        dto.setValues(valuesDto);
        return dto;
    }

    @Transactional(readOnly = true)
    public LibraryParameterDTO getOneDTO(final String id) {
        final LibraryParameter libraryParameter = libraryParameterService.findLibraryParameterWithDependencies(id);
        return LibraryParameterMapper.INSTANCE.libraryParameterToLibraryParameterDTO(libraryParameter);
    }

    @Transactional(readOnly = true)
    public LibraryParameterValuedDTO getCinesDefaultValues(final Library lib) {

        final LibraryParameter libraryParameter = libraryParameterService.findCinesParameterForLibrary(lib);
        if (libraryParameter != null) {
            final List<LibraryParameterValueCinesDTO> valuesDto = new ArrayList<>();
            final LibraryParameterValuedDTO dto = LibraryParameterMapper.INSTANCE.libParamToLibParamValuedDTO(libraryParameter);
            libraryParameter.getValues().stream().map(LibraryParameterValueCines.class::cast).forEach(val -> {
                valuesDto.add(LibraryParameterMapper.INSTANCE.libParamCinesValueToDTO(val));
            });
            dto.setValues(valuesDto);
            return dto;
        }
        return null;
    }

    private PgcnError buildError(final PgcnErrorCode pgcnErrorCode) {
        final PgcnError.Builder builder = new PgcnError.Builder();
        switch (pgcnErrorCode) {
            default:
                break;
        }
        return builder.build();
    }

}

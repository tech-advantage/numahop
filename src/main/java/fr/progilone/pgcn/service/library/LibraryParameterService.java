package fr.progilone.pgcn.service.library;

import fr.progilone.pgcn.domain.library.AbstractLibraryParameterValue;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.library.LibraryParameter;
import fr.progilone.pgcn.domain.library.LibraryParameter.LibraryParameterType;
import fr.progilone.pgcn.domain.library.LibraryParameterValueCines;
import fr.progilone.pgcn.repository.library.LibraryParameterCinesRepository;
import fr.progilone.pgcn.repository.library.LibraryParameterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryParameterService {

    private final LibraryParameterRepository libraryParameterRepository;
    // Repository dédiés
    private final LibraryParameterCinesRepository cinesParameterRepository;

    public LibraryParameterService(final LibraryParameterRepository libraryParameterRepository, final LibraryParameterCinesRepository cinesParameterRepository) {
        this.libraryParameterRepository = libraryParameterRepository;
        this.cinesParameterRepository = cinesParameterRepository;
    }

    @Transactional(readOnly = true)
    public LibraryParameter findCinesParameterForLibrary(final Library lib) {
        if (lib == null) {
            return null;
        }
        return libraryParameterRepository.getOneByTypeAndLibrary(LibraryParameterType.CINES_EXPORT, lib);
    }

    @Transactional
    public void delete(final LibraryParameter param) {
        libraryParameterRepository.delete(param);
    }

    @Transactional
    public void delete(final String identifer) {
        libraryParameterRepository.deleteById(identifer);
    }

    @Transactional
    public LibraryParameter save(final LibraryParameter param) {
        final LibraryParameter savedLibParam = libraryParameterRepository.save(param);

        switch (savedLibParam.getType()) {
            case CINES_EXPORT:
                saveCinesValueParameter(savedLibParam);
                break;
            default:
                break;
        }
        return savedLibParam;
    }

    private void saveCinesValueParameter(final LibraryParameter param) {
        for (final AbstractLibraryParameterValue value : param.getValues()) {
            cinesParameterRepository.save((LibraryParameterValueCines) value);
        }
    }

    public LibraryParameter findLibraryParameterWithDependencies(final String identifier) {
        return libraryParameterRepository.getOneByIdentifier(identifier);
    }

    @Transactional(readOnly = true)
    public LibraryParameter findCinesDefaultValuesForLibrary(final Library lib) {

        final LibraryParameter libParam = libraryParameterRepository.getByTypeAndLibraryWithValues(LibraryParameterType.CINES_EXPORT, lib);

        return libraryParameterRepository.getByTypeAndLibraryWithValues(LibraryParameterType.CINES_EXPORT, lib);
    }
}

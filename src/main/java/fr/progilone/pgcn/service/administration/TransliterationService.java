package fr.progilone.pgcn.service.administration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import fr.progilone.pgcn.domain.administration.Transliteration;
import fr.progilone.pgcn.repository.administration.TransliterationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sébastien on 29/06/2017.
 */
@Service
public class TransliterationService {

    private static final Logger LOG = LoggerFactory.getLogger(TransliterationService.class);

    private final TransliterationRepository transliterationRepository;

    private final LoadingCache<Transliteration.TransliterationId, String> cache = CacheBuilder.newBuilder()
                                                                                              .maximumSize(10000)
                                                                                              .expireAfterWrite(1, TimeUnit.HOURS)
                                                                                              .build(new CacheLoader<Transliteration.TransliterationId, String>() {
                                                                                                  @Override
                                                                                                  public String load(final Transliteration.TransliterationId id) {
                                                                                                      final Transliteration transliteration =
                                                                                                          transliterationRepository.findOne(id);
                                                                                                      return transliteration != null ?
                                                                                                             transliteration.getValue() :
                                                                                                             id.getCode();
                                                                                                  }
                                                                                              });

    @Autowired
    public TransliterationService(final TransliterationRepository transliterationRepository) {
        this.transliterationRepository = transliterationRepository;
    }

    @Transactional(readOnly = true)
    public String getValue(final Transliteration.Type type, final String code) {
        return cache.getUnchecked(new Transliteration.TransliterationId(type, code));
    }

    @Transactional(readOnly = true)
    public String getValue(final String type, final String code) {
        try {
            final Transliteration.Type typeValue = Transliteration.Type.valueOf(type);
            return getValue(typeValue, code);

        } catch (IllegalArgumentException e) {
            LOG.warn(e.getMessage(), e);
            return code;
        }
    }

    @Transactional(readOnly = true)
    public String getFunction(final String code) {
        return getValue(Transliteration.Type.FUNCTION, code);
    }
}

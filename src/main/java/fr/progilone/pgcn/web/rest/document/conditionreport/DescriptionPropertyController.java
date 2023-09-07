package fr.progilone.pgcn.web.rest.document.conditionreport;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.COND_REPORT_HAB0;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.COND_REPORT_HAB6;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.document.conditionreport.DescriptionPropertyService;
import fr.progilone.pgcn.service.document.conditionreport.ui.UiPropertyConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/rest/condreport_desc_prop")
public class DescriptionPropertyController extends AbstractRestController {

    private final DescriptionPropertyService descriptionPropertyService;
    private final UiPropertyConfigurationService propertyConfigurationService;

    @Autowired
    public DescriptionPropertyController(final DescriptionPropertyService descriptionPropertyService, UiPropertyConfigurationService propertyConfigurationService) {
        this.descriptionPropertyService = descriptionPropertyService;
        this.propertyConfigurationService = propertyConfigurationService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB6)
    public ResponseEntity<DescriptionProperty> create(@RequestBody final DescriptionProperty property) throws PgcnException {
        return new ResponseEntity<>(descriptionPropertyService.save(property), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(COND_REPORT_HAB6)
    public void delete(@PathVariable final String identifier) {
        descriptionPropertyService.delete(identifier);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({COND_REPORT_HAB0,
                   COND_REPORT_HAB6})
    public ResponseEntity<List<DescriptionProperty>> findAll() {
        return createResponseEntity(descriptionPropertyService.findAll());
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = "getAllWithFakes")
    @Timed
    @RolesAllowed({COND_REPORT_HAB0,
                   COND_REPORT_HAB6})
    public List<DescriptionProperty> getAllWithFakesAndShowCreationByLibrary(final HttpServletRequest request, @RequestParam(name = "library") final String libraryId) {
        List<DescriptionProperty> propertiesList = descriptionPropertyService.findAll();

        addFakePropertiesInList(propertiesList);

        // get all the property_config by library with ShowOnCreation to false
        Set<String> propertiesConfDto = propertyConfigurationService.findByLibraryAndNotShowOnCreation(libraryId)
                                                                    .stream()
                                                                    .map(conf -> StringUtils.defaultIfBlank(conf.getDescPropertyId(), conf.getInternalProperty()))
                                                                    .collect(Collectors.toSet());

        return propertiesList.stream().filter(prop -> !propertiesConfDto.contains(prop.getIdentifier())).collect(Collectors.toList());
    }

    private void addFakePropertiesInList(List<DescriptionProperty> properties) {
        /**
         * This list of fakes properties have to be the same of the mappinfSrvc.js AND condreportSrvc.js
         */
        final DescriptionProperty fakeDimension = new DescriptionProperty();
        fakeDimension.setLabel("Dimensions du document (H/L/P, mm)");
        fakeDimension.setIdentifier("DIMENSION");
        fakeDimension.setCode("DIMENSION");
        fakeDimension.setType(DescriptionProperty.Type.DESCRIPTION);
        properties.add(fakeDimension);

        final DescriptionProperty fakeBidingDesc = new DescriptionProperty();
        fakeBidingDesc.setLabel("Autres informations");
        fakeBidingDesc.setIdentifier("BINDING_DESC");
        fakeBidingDesc.setCode("BINDING_DESC");
        fakeBidingDesc.setType(DescriptionProperty.Type.BINDING);
        properties.add(fakeBidingDesc);

        final DescriptionProperty fakeBodyDesc = new DescriptionProperty();
        fakeBodyDesc.setLabel("Autres informations");
        fakeBodyDesc.setIdentifier("BODY_DESC");
        fakeBodyDesc.setCode("BODY_DESC");
        fakeBodyDesc.setType(DescriptionProperty.Type.VIGILANCE);
        properties.add(fakeBodyDesc);

        final DescriptionProperty fakeInsurance = new DescriptionProperty();
        fakeInsurance.setLabel("Assurance");
        fakeInsurance.setIdentifier("INSURANCE");
        fakeInsurance.setCode("INSURANCE");
        fakeInsurance.setType(DescriptionProperty.Type.DESCRIPTION);
        properties.add(fakeInsurance);

        final DescriptionProperty fakeViewBinding = new DescriptionProperty();
        fakeViewBinding.setLabel("Estimation du nombre de vues - Reliure");
        fakeViewBinding.setIdentifier("NB_VIEW_BINDING");
        fakeViewBinding.setCode("NB_VIEW_BINDING");
        fakeViewBinding.setType(DescriptionProperty.Type.DESCRIPTION);
        properties.add(fakeViewBinding);

        final DescriptionProperty fakeViewBody = new DescriptionProperty();
        fakeViewBody.setLabel("Estimation du nombre de vues - Corps d'ouvrage");
        fakeViewBody.setIdentifier("NB_VIEW_BODY");
        fakeViewBody.setCode("NB_VIEW_BODY");
        fakeViewBody.setType(DescriptionProperty.Type.DESCRIPTION);
        properties.add(fakeViewBody);

        final DescriptionProperty fakeViewAdditionnal = new DescriptionProperty();
        fakeViewAdditionnal.setLabel("Estimation du nombre de vues - Vues Supplémentaires");
        fakeViewAdditionnal.setIdentifier("NB_VIEW_ADDITIONNAL");
        fakeViewAdditionnal.setCode("NB_VIEW_ADDITIONNAL");
        fakeViewAdditionnal.setType(DescriptionProperty.Type.DESCRIPTION);
        properties.add(fakeViewAdditionnal);

        final DescriptionProperty fakeSynthesis = new DescriptionProperty();
        fakeSynthesis.setLabel("Synthèse");
        fakeSynthesis.setIdentifier("ADDITIONNAL_DESC");
        fakeSynthesis.setCode("ADDITIONNAL_DESC");
        fakeSynthesis.setType(DescriptionProperty.Type.DESCRIPTION);
        properties.add(fakeSynthesis);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(COND_REPORT_HAB6)
    public ResponseEntity<DescriptionProperty> update(@RequestBody final DescriptionProperty value) throws PgcnException {
        return new ResponseEntity<>(descriptionPropertyService.save(value), HttpStatus.OK);
    }
}

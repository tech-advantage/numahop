package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.Description;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportDetailDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportValueDTO;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class ConditionReportDetailMapperTest {

    private static final ConditionReportDetailMapper DETAIL_MAPPER = ConditionReportDetailMapper.INSTANCE;

    @Test
    public void testDetailToDTO() {
        final ConditionReportDetail detail = new ConditionReportDetail();
        detail.setDate(LocalDate.of(2017, 10, 11));
        detail.setInsurance(12345.6789D);

        final Description descBind = getDescription(DescriptionProperty.Type.BINDING);
        final Description descDesc = getDescription(DescriptionProperty.Type.DESCRIPTION);
        final Description descNumb = getDescription(DescriptionProperty.Type.NUMBERING);
        final Description descVigi = getDescription(DescriptionProperty.Type.VIGILANCE);
        detail.addDescription(descBind);
        detail.addDescription(descDesc);
        detail.addDescription(descNumb);
        detail.addDescription(descVigi);

        final ConditionReportDetailDTO actual = DETAIL_MAPPER.detailToDTO(detail);

        assertEquals("11/10/2017", actual.getDate());
        assertEquals("12\u00A0345,68", actual.getInsurance());

        checkDescDTO(descBind, actual.getBindings());
        checkDescDTO(descDesc, actual.getDescriptions());
        checkDescDTO(descNumb, actual.getNumberings());
        checkDescDTO(descVigi, actual.getVigilances());
    }

    private void checkDescDTO(final Description desc, final List<ConditionReportValueDTO> descriptions) {
        assertEquals(1, descriptions.size());
        final ConditionReportValueDTO value = descriptions.get(0);
        assertEquals(desc.getProperty().getIdentifier(), value.getPropertyId());
        assertEquals(desc.getProperty().getCode(), value.getPropertyCode());
        assertEquals(desc.getProperty().getLabel(), value.getPropertyLabel());
        assertEquals(desc.getProperty().getType().name(), value.getPropertyType());
        assertEquals(desc.getValue().getLabel(), value.getValue());
    }

    private Description getDescription(final DescriptionProperty.Type type) {
        final DescriptionValue value = new DescriptionValue();
        value.setLabel(type.name() + "#value");

        final DescriptionProperty property = new DescriptionProperty();
        property.setIdentifier(type.name() + "#propid");
        property.setCode(type.name() + "#code");
        property.setLabel(type.name() + "#label");
        property.setType(type);

        final Description desc = new Description();
        desc.setComment(type.name() + "#comment");
        desc.setProperty(property);
        desc.setValue(value);
        return desc;
    }
}

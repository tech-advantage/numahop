package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit_;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DocUnitSerializer extends StdSerializer<DocUnit> {

    public DocUnitSerializer() {
        super(DocUnit.class);
    }

    @Override
    public void serialize(final DocUnit docUnit, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(DocUnit_.identifier.getName(), docUnit.getIdentifier());
        jgen.writeObjectField(DocUnit_.createdDate.getName(), docUnit.getCreatedDate());
        jgen.writeObjectField(DocUnit_.lastModifiedDate.getName(), docUnit.getLastModifiedDate());
        jgen.writeStringField(DocUnit_.pgcnId.getName(), docUnit.getPgcnId());
        jgen.writeStringField(DocUnit_.label.getName(), docUnit.getLabel());
        jgen.writeStringField(DocUnit_.type.getName(), docUnit.getType());
        jgen.writeStringField(DocUnit_.arkUrl.getName(), docUnit.getArkUrl());
        jgen.writeObjectField(DocUnit_.archivable.getName(), docUnit.isArchivable());
        jgen.writeObjectField(DocUnit_.distributable.getName(), docUnit.isDistributable());
        jgen.writeObjectField(DocUnit_.rights.getName(), docUnit.getRights());
        jgen.writeObjectField(DocUnit_.embargo.getName(), docUnit.getEmbargo());
        jgen.writeObjectField(DocUnit_.checkDelay.getName(), docUnit.getCheckDelay());
        jgen.writeObjectField(DocUnit_.checkEndTime.getName(), docUnit.getCheckEndTime());
        jgen.writeObjectField(DocUnit_.collectionIA.getName(), docUnit.getCollectionIA());
        jgen.writeObjectField(DocUnit_.planClassementPAC.getName(), docUnit.getPlanClassementPAC());
        jgen.writeObjectField(DocUnit_.physicalDocuments.getName(), docUnit.getPhysicalDocuments());
        jgen.writeObjectField(DocUnit_.library.getName(), docUnit.getLibrary());
        jgen.writeStringField(DocUnit_.lotId.getName(), docUnit.getLotId());
        jgen.writeStringField(DocUnit_.projectId.getName(), docUnit.getProjectId());
        jgen.writeNumberField("nbDigitalDocuments", docUnit.getNbDigitalDocuments());
        jgen.writeObjectField("workflowStateKeys", docUnit.getWorkflowStateKeys());
        jgen.writeObjectField("latestDeliveryDate", docUnit.getLatestDeliveryDate());
        jgen.writeNumberField("masterSize", docUnit.getMasterSize() != null ? docUnit.getMasterSize() : 0L);

        serializeParent(docUnit, jgen);
        serializeChildren(docUnit, jgen);
        serializeSibling(docUnit, jgen);
        // Suggestions
        if (docUnit.getSuggest() != null) {
            jgen.writeObjectField("suggest", docUnit.getSuggest());
        }
        jgen.writeEndObject();
    }

    private void serializeParent(final DocUnit docUnit, final JsonGenerator jgen) throws IOException {
        if (docUnit.getParent() != null) {
            jgen.writeObjectFieldStart(DocUnit_.parent.getName());
            jgen.writeStringField(DocUnit_.identifier.getName(), docUnit.getParent().getIdentifier());
            jgen.writeEndObject();
        }
    }

    private void serializeChildren(final DocUnit docUnit, final JsonGenerator jgen) throws IOException {
        final List<String> children = docUnit.getChildren().stream().map(DocUnit::getIdentifier).collect(Collectors.toList());
        if (!children.isEmpty()) {
            jgen.writeArrayFieldStart(DocUnit_.children.getName());
            for (final String child : children) {
                jgen.writeStartObject();
                jgen.writeStringField(DocUnit_.identifier.getName(), child);
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
        }
    }

    private void serializeSibling(final DocUnit docUnit, final JsonGenerator jgen) throws IOException {
        if (docUnit.getSibling() != null) {
            final List<String> siblings = docUnit.getSibling()
                                                 .getDocUnits()
                                                 .stream()
                                                 .filter(sib -> !StringUtils.equals(sib.getIdentifier(), docUnit.getIdentifier()))
                                                 .map(DocUnit::getIdentifier)
                                                 .collect(Collectors.toList());

            if (!siblings.isEmpty()) {
                jgen.writeObjectField(DocUnit_.sibling.getName(), siblings);
            }
        }
    }
}

package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.project.Project_;

import java.io.IOException;

public class ProjectSerializer extends StdSerializer<Project> {

    public ProjectSerializer() {
        super(Project.class);
    }

    @Override
    public void serialize(final Project project, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(Project_.identifier.getName(), project.getIdentifier());
        jgen.writeStringField(Project_.name.getName(), project.getName());
        jgen.writeObjectField(Project_.library.getName(), project.getLibrary());
        jgen.writeObjectField(Project_.provider.getName(), project.getProvider());
        jgen.writeObjectField(Project_.startDate.getName(), project.getStartDate());
        jgen.writeObjectField(Project_.forecastEndDate.getName(), project.getForecastEndDate());
        jgen.writeObjectField(Project_.realEndDate.getName(), project.getRealEndDate());
        jgen.writeBooleanField(Project_.active.getName(), project.isActive());
        jgen.writeObjectField(Project_.status.getName(), project.getStatus());
        jgen.writeObjectField(Project_.associatedLibraries.getName(), project.getAssociatedLibraries());
        jgen.writeObjectField(Project_.associatedUsers.getName(), project.getAssociatedUsers());
        jgen.writeEndObject();
    }
}

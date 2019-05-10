
package fr.progilone.pgcn.service.exchange.iiif.manifest;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "@id",
    "@type"
})
public class ViewingHint {

    @JsonProperty("@id")
    private String id;
    @JsonProperty("@type")
    private String type;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public ViewingHint() {
    }

    /**
     *
     * @param id
     * @param type
     */
    public ViewingHint(String id, String type) {
        super();
        this.id = id;
        this.type = type;
    }

    @JsonProperty("@id")
    public String getId() {
        return id;
    }

    @JsonProperty("@id")
    public void setId(String id) {
        this.id = id;
    }

    public ViewingHint withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("@type")
    public String getType() {
        return type;
    }

    @JsonProperty("@type")
    public void setType(String type) {
        this.type = type;
    }

    public ViewingHint withType(String type) {
        this.type = type;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public ViewingHint withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

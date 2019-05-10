
package fr.progilone.pgcn.service.exchange.iiif.manifest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "@context"
})
public class Manifest {

    @JsonProperty("@context")
    @Valid
    private List<Context> context = new ArrayList<>();
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Manifest() {
    }

    /**
     *
     * @param context
     */
    public Manifest(List<Context> context) {
        super();
        this.context = context;
    }

    @JsonProperty("@context")
    public List<Context> getContext() {
        return context;
    }

    @JsonProperty("@context")
    public void setContext(List<Context> context) {
        this.context = context;
    }

    public Manifest withContext(List<Context> context) {
        this.context = context;
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

    public Manifest withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;

/**
 * Created by lebouchp on 25/01/2017.
 */
public class SimplePhysicalDocumentDTO extends AbstractDTO {

    private String identifier;
    private String name;
    private String digitalId;
    private SimpleTrainDTO train;

    public SimplePhysicalDocumentDTO(){}

    public SimplePhysicalDocumentDTO(String identifier, String name, String digitalId, SimpleTrainDTO train) {
        this.identifier = identifier;
        this.name = name;
        this.digitalId = digitalId;
        this.train = train;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public SimpleTrainDTO getTrain() {
        return train;
    }

    public void setTrain(SimpleTrainDTO train) {
        this.train = train;
    }
}

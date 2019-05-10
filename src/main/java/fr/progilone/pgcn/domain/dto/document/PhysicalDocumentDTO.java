package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;

public class PhysicalDocumentDTO extends AbstractVersionedDTO {

    private String identifier;
    private String name;
    private String digitalId;
    private Integer totalPage;
    private SimpleTrainDTO train;

    public PhysicalDocumentDTO(String identifier, String name, String digitalId, Integer totalPage, SimpleTrainDTO train) {
        this.identifier = identifier;
        this.name = name;
        this.digitalId = digitalId;
        this.totalPage = totalPage;
        this.train = train;
    }

    public PhysicalDocumentDTO() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public SimpleTrainDTO getTrain() {
        return train;
    }

    public void setTrain(SimpleTrainDTO train) {
        this.train = train;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}

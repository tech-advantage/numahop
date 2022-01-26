package fr.progilone.pgcn.domain.dto.document;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;

public class PhysicalDocumentDTO extends AbstractVersionedDTO {

    private String identifier;
    private String name;
    private String digitalId;
    private Integer totalPage;
    private SimpleTrainDTO train;
    private String commentaire;

    public PhysicalDocumentDTO(final String identifier, final String name, final String digitalId, final Integer totalPage, final SimpleTrainDTO train, final String commentaire) {
        this.identifier = identifier;
        this.name = name;
        this.digitalId = digitalId;
        this.totalPage = totalPage;
        this.train = train;
        this.commentaire = commentaire;
    }

    public PhysicalDocumentDTO() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(final String digitalId) {
        this.digitalId = digitalId;
    }

    public SimpleTrainDTO getTrain() {
        return train;
    }

    public void setTrain(final SimpleTrainDTO train) {
        this.train = train;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(final Integer totalPage) {
        this.totalPage = totalPage;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(final String commentaire) {
        this.commentaire = commentaire;
    }
}

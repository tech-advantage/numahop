package fr.progilone.pgcn.service.exchange.marc;

import fr.progilone.pgcn.domain.document.DocUnit;
import java.util.Objects;

public class DocUnitWrapper {

    private DocUnit docUnit;
    private String parentKey;

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(final String parentKey) {
        this.parentKey = parentKey;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DocUnitWrapper that = (DocUnitWrapper) o;
        return Objects.equals(docUnit, that.docUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docUnit);
    }
}

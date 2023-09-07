package fr.progilone.pgcn.domain.dto.document.conditionreport;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;

public class ConditionReportDetailVelocityDTO {

    private String identifier;
    private String type;
    private String libWriterName;
    private String libWriterFunction;
    private String provWriterName;
    private String provWriterFunction;
    private String date;
    private String comment;
    private int position;
    private int nbViewBinding = 0;
    private int nbViewBody = 0;
    private int nbViewAdditionnal = 0;
    private int nbViewTotal = 0;
    private Integer dim1;
    private Integer dim2;
    private Integer dim3;
    private String bindingDesc;
    private String bodyDesc;
    private String additionnalDesc;
    private String insurance;

    private final List<ConditionReportValueVelocityDTO> descriptions = new ArrayList<>();
    private final List<ConditionReportValueVelocityDTO> bindings = new ArrayList<>();
    private final List<ConditionReportValueVelocityDTO> numberings = new ArrayList<>();
    private final List<ConditionReportValueVelocityDTO> vigilances = new ArrayList<>();
    private final List<ConditionReportValueVelocityDTO> states = new ArrayList<>();
    private final List<ConditionReportValueVelocityDTO> types = new ArrayList<>();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getLibWriterName() {
        return libWriterName;
    }

    public void setLibWriterName(final String libWriterName) {
        this.libWriterName = libWriterName;
    }

    public String getLibWriterFunction() {
        return libWriterFunction;
    }

    public void setLibWriterFunction(final String libWriterFunction) {
        this.libWriterFunction = libWriterFunction;
    }

    public String getProvWriterName() {
        return provWriterName;
    }

    public void setProvWriterName(final String provWriterName) {
        this.provWriterName = provWriterName;
    }

    public String getProvWriterFunction() {
        return provWriterFunction;
    }

    public void setProvWriterFunction(final String provWriterFunction) {
        this.provWriterFunction = provWriterFunction;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(final int position) {
        this.position = position;
    }

    public int getNbViewBinding() {
        return nbViewBinding;
    }

    public void setNbViewBinding(final int nbViewBinding) {
        this.nbViewBinding = nbViewBinding;
    }

    public int getNbViewBody() {
        return nbViewBody;
    }

    public void setNbViewBody(final int nbViewBody) {
        this.nbViewBody = nbViewBody;
    }

    public int getNbViewAdditionnal() {
        return nbViewAdditionnal;
    }

    public void setNbViewAdditionnal(final int nbViewAdditionnal) {
        this.nbViewAdditionnal = nbViewAdditionnal;
    }

    public int getNbViewTotal() {
        return nbViewTotal;
    }

    public void setNbViewTotal(final int nbViewTotal) {
        this.nbViewTotal = nbViewTotal;
    }

    public Integer getDim1() {
        return dim1;
    }

    public void setDim1(final Integer dim1) {
        this.dim1 = dim1;
    }

    public Integer getDim2() {
        return dim2;
    }

    public void setDim2(final Integer dim2) {
        this.dim2 = dim2;
    }

    public Integer getDim3() {
        return dim3;
    }

    public void setDim3(final Integer dim3) {
        this.dim3 = dim3;
    }

    public String getBindingDesc() {
        return bindingDesc;
    }

    public void setBindingDesc(final String bindingDesc) {
        this.bindingDesc = bindingDesc;
    }

    public String getBodyDesc() {
        return bodyDesc;
    }

    public void setBodyDesc(final String bodyDesc) {
        this.bodyDesc = bodyDesc;
    }

    public String getAdditionnalDesc() {
        return additionnalDesc;
    }

    public void setAdditionnalDesc(final String additionnalDesc) {
        this.additionnalDesc = additionnalDesc;
    }

    public List<ConditionReportValueVelocityDTO> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(final List<ConditionReportValueVelocityDTO> descriptions) {
        this.descriptions.clear();
        this.descriptions.addAll(descriptions);
    }

    public List<ConditionReportValueVelocityDTO> getBindings() {
        return bindings;
    }

    public void setBindings(final List<ConditionReportValueVelocityDTO> bindings) {
        this.bindings.clear();
        this.bindings.addAll(bindings);
    }

    public List<ConditionReportValueVelocityDTO> getVigilances() {
        return vigilances;
    }

    public void setVigilances(final List<ConditionReportValueVelocityDTO> vigilances) {
        this.vigilances.clear();
        this.vigilances.addAll(vigilances);
    }

    public List<ConditionReportValueVelocityDTO> getNumberings() {
        return numberings;
    }

    public void setNumberings(final List<ConditionReportValueVelocityDTO> numberings) {
        this.numberings.clear();
        this.numberings.addAll(numberings);
    }

    public List<ConditionReportValueVelocityDTO> getStates() {
        return states;
    }

    public void setStates(final List<ConditionReportValueVelocityDTO> states) {
        this.states.clear();
        this.states.addAll(states);
    }

    public List<ConditionReportValueVelocityDTO> getTypes() {
        return types;
    }

    public void setTypes(final List<ConditionReportValueVelocityDTO> types) {
        this.types.clear();
        this.types.addAll(types);
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(final String insurance) {
        this.insurance = insurance;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("identifier", identifier).toString();
    }
}

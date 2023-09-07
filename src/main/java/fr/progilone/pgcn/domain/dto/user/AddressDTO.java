package fr.progilone.pgcn.domain.dto.user;

import fr.progilone.pgcn.domain.user.Address;

/**
 * Adresse DTO
 *
 * @author jbrunet
 * @see Address
 *
 */
public class AddressDTO {

    private String identifier;
    private String label;
    private String address1;
    private String address2;
    private String address3;
    private String complement;
    private String postcode;
    private String city;
    private String country;
    private Long version;

    public AddressDTO(final String identifier,
                      final String label,
                      final String address1,
                      final String address2,
                      final String address3,
                      final String complement,
                      final String postcode,
                      final String city,
                      final String country,
                      final Long version) {
        super();
        this.identifier = identifier;
        this.label = label;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.complement = complement;
        this.postcode = postcode;
        this.city = city;
        this.country = country;
        this.version = version;
    }

    public AddressDTO() {
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final String getLabel() {
        return label;
    }

    public final String getAddress1() {
        return address1;
    }

    public final String getAddress2() {
        return address2;
    }

    public final String getAddress3() {
        return address3;
    }

    public final String getComplement() {
        return complement;
    }

    public final String getPostcode() {
        return postcode;
    }

    public final String getCity() {
        return city;
    }

    public final String getCountry() {
        return country;
    }

    public final Long getVersion() {
        return version;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Builder pour la classe AddressDTO
     *
     * @author jbrunet
     */
    public static final class Builder {

        private String identifier;
        private String label;
        private String address1;
        private String address2;
        private String address3;
        private String complement;
        private String postcode;
        private String city;
        private String country;
        private Long version;

        public Builder reinit() {
            this.identifier = null;
            this.label = null;
            this.address1 = null;
            this.address2 = null;
            this.address3 = null;
            this.complement = null;
            this.postcode = null;
            this.city = null;
            this.country = null;
            this.version = null;
            return this;
        }

        public Builder setIdentifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setLabel(final String label) {
            this.label = label;
            return this;
        }

        public Builder setAddress1(final String address1) {
            this.address1 = address1;
            return this;
        }

        public Builder setAddress2(final String address2) {
            this.address2 = address2;
            return this;
        }

        public Builder setAddress3(final String address3) {
            this.address3 = address3;
            return this;
        }

        public Builder setComplement(final String complement) {
            this.complement = complement;
            return this;
        }

        public Builder setPostcode(final String postcode) {
            this.postcode = postcode;
            return this;
        }

        public Builder setCity(final String city) {
            this.city = city;
            return this;
        }

        public Builder setCountry(final String country) {
            this.country = country;
            return this;
        }

        public Builder setVersion(final Long version) {
            this.version = version;
            return this;
        }

        public AddressDTO build() {
            return new AddressDTO(identifier, label, address1, address2, address3, complement, postcode, city, country, version);
        }
    }
}

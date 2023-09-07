package fr.progilone.pgcn.domain.administration.exchange.z3950;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.exchange.DataEncoding;
import jakarta.persistence.*;

@Entity
@Table(name = Z3950Server.TABLE_NAME)
public class Z3950Server extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_z3950_server";

    /**
     * Nom du serveur Z39.50
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * Nom d'hôte
     */
    @Column(name = "host", nullable = false)
    private String host;

    /**
     * Port du serveur
     */
    @Column(name = "port", nullable = false)
    private Integer port;

    /**
     * Nom de la base de donnée
     */
    @Column(name = "dbname", nullable = false)
    private String database;

    /**
     * Login utilisateur
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * Mot de passe utilisateur
     */
    @Column(name = "password")
    private String password;

    /**
     * Le serveur est-il inclus lors d'une recherche Z 39.50
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Format des données (MARC21, UNIMARC...)
     */
    @Column(name = "data_format", nullable = false)
    @Enumerated(EnumType.STRING)
    private DataFormat dataFormat;

    /**
     * Encodage des données pour l'envoi des données (UTF8, ...)
     */
    @Column(name = "data_encoding", nullable = false)
    @Enumerated(EnumType.STRING)
    private DataEncoding dataEncoding;

    @Column(name = "record_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecordType recordType;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(final String database) {
        this.database = database;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(final DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    public DataEncoding getDataEncoding() {
        return dataEncoding;
    }

    public void setDataEncoding(final DataEncoding dataEncoding) {
        this.dataEncoding = dataEncoding;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(final RecordType recordType) {
        this.recordType = recordType;
    }

    @Override
    public String toString() {
        return "Z3950Server{" + "name='"
               + name
               + '\''
               + ", host='"
               + host
               + '\''
               + ", port="
               + port
               + ", database='"
               + database
               + '\''
               + ", userId='"
               + userId
               + '\''
               + ", active="
               + active
               + ", dataFormat="
               + dataFormat
               + ", dataEncoding="
               + dataEncoding
               + ", recordType="
               + recordType
               + '}';
    }
}

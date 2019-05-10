package fr.progilone.pgcn.service.exchange.oaipmh;

public final class OaiPmhRequest {

    private final String baseUrl;
    private final String metadataPrefix;
    private final String from;
    private final String to;
    private final String set;

    public OaiPmhRequest(final String baseUrl, final String from, final String to, final String set) {
        this.baseUrl = baseUrl;
        this.metadataPrefix = "oai_dc";
        this.from = from;
        this.to = to;
        this.set = set;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSet() {
        return set;
    }
}

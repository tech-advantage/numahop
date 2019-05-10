package fr.progilone.pgcn.domain.dto.exchange;

import fr.progilone.pgcn.domain.dto.administration.z3950.Z3950ServerDTO;

public class Z3950RecordDTO {

    private Z3950ServerDTO z3950Server;
    private String title;
    private String author;
    private String isbn;
    private String issn;
    private String marcXml;

    public Z3950ServerDTO getZ3950Server() {
        return z3950Server;
    }

    public void setZ3950Server(Z3950ServerDTO z3950Server) {
        this.z3950Server = z3950Server;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getMarcXml() {
        return marcXml;
    }

    public void setMarcXml(String marcXml) {
        this.marcXml = marcXml;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }
}

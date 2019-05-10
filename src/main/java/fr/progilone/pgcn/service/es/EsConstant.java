package fr.progilone.pgcn.service.es;

public interface EsConstant {

    String ANALYZER_KEYWORD = "keyword";
    String ANALYZER_CI_AI = "ci_ai";
    String ANALYZER_CI_AS = "ci_as";
    String ANALYZER_PHRASE = "ci_ai_phr";

    String SUBFIELD_RAW = "raw";
    String SUBFIELD_CI_AI = "ci_ai";
    String SUBFIELD_CI_AS = "ci_as";
    String SUBFIELD_PHRASE = "ci_ai_phr";

    String SUGGEST_FIELD = "suggest";
    String SUGGEST_CTX_LIBRARY = "library";
    String SUGGEST_CTX_GLOBAL = "___global___";
}

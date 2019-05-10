package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.administration.CinesPAC_;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection_;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.BibliographicRecord_;
import fr.progilone.pgcn.domain.document.DocPropertyType_;
import fr.progilone.pgcn.domain.document.DocProperty_;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit_;
import fr.progilone.pgcn.domain.document.PhysicalDocument_;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport_;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport_;
import fr.progilone.pgcn.domain.library.Library_;
import fr.progilone.pgcn.repository.es.helper.EsQueryBuilder;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.children.ChildrenBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.NestedBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.progilone.pgcn.repository.es.helper.EsQueryHelper.*;

public class EsDocUnitRepositoryImpl extends AbstractEsRepository<DocUnit> implements EsDocUnitRepositoryCustom {

    @Autowired
    public EsDocUnitRepositoryImpl(final ElasticsearchTemplate elasticsearchTemplate, final EntityMapper entityMapper) {
        super(DocUnit.class, elasticsearchTemplate, entityMapper);
    }

    @Override
    protected QueryBuilder getSearchQueryBuilder(final EsSearchOperation searchOp, final boolean fuzzy) {
        final EsQueryBuilder builder = new EsQueryBuilder();
        final String search = searchOp.getSearch();
        final String[] index = readIndex(searchOp.getIndex(), INDEX_DOCUNIT);
        final String obj = index[0];
        final String field = index[1];

        if (StringUtils.isNotBlank(search)) {
            // Recherche par défaut
            if (StringUtils.equals(field, "default")) {
                addDefaultSearch(builder, search, fuzzy);
            }
            // Recherche par champ
            else {
                switch (obj) {
                    case INDEX_DOCUNIT:
                        addDocUnitSearch(builder, search, field, fuzzy);
                        break;
                    case INDEX_RECORD:
                        addRecordsearch(builder, search, field, fuzzy);
                        break;
                    case INDEX_PROPERTY:
                        addPropertySearch(builder, search, field, fuzzy);
                        break;
                    case INDEX_PHYSDOC:
                        addPhysicalDocSearch(builder, search, field, fuzzy);
                        break;
                    case INDEX_CINES:
                        addCinesSearch(builder, search, field, fuzzy);
                        break;
                    case INDEX_IA:
                        addIaSearch(builder, search, field, fuzzy);
                        break;
                }
            }
        }
        return builder.build();
    }

    @Override
    protected Optional<QueryBuilder> getLibraryQueryBuilder(final List<String> libraries) {
        if (CollectionUtils.isNotEmpty(libraries)) {
            return Optional.of(QueryBuilders.termsQuery(path(DocUnit_.library, Library_.identifier), libraries));
        }
        return Optional.empty();
    }

    @Override
    protected Field[] getHighlightField() {
        return new Field[] {new Field("label"), new Field("pgcnId")};
    }

    @Override
    protected QueryBuilder getFilterQueryBuilder(final String searchField, final List<String> values) {
        final int pos = searchField.indexOf(':');
        final String type = pos >= 0 ? searchField.substring(0, pos) : "DOCUNIT";
        final String field = pos >= 0 ? searchField.substring(pos + 1) : searchField;

        switch (type) {
            case "RECORD_PROPERTY":
                // docunit > bib_record > properties [type = field] > value.raw = values
                final BoolQueryBuilder searchQry = QueryBuilders.boolQuery()
                                                                .filter(QueryBuilders.termsQuery("properties.type.identifier", field))
                                                                .filter(QueryBuilders.termsQuery("properties.value.raw",
                                                                                                 values.toArray(new String[0])));
                return QueryBuilders.hasChildQuery(BibliographicRecord.ES_TYPE, QueryBuilders.nestedQuery("properties", searchQry));

            case "DOCUNIT":
            default:
                return super.getFilterQueryBuilder(field, values);
        }
    }

    @Override
    protected List<AbstractAggregationBuilder> getAggregationBuilders() {
        return Stream.of("DOCUNIT:type",
                         "DOCUNIT:archivable",
                         "DOCUNIT:distributable",
                         "RECORD_PROPERTY:contributor",
                         "RECORD_PROPERTY:coverage",
                         "RECORD_PROPERTY:creator",
                         "RECORD_PROPERTY:language",
                         "RECORD_PROPERTY:publisher",
                         "RECORD_PROPERTY:relation",
                         "RECORD_PROPERTY:rights",
                         "RECORD_PROPERTY:subject",
                         "RECORD_PROPERTY:type").map(this::getAggregationBuilder).collect(Collectors.toList());
    }

    protected AbstractAggregationBuilder getAggregationBuilder(final String aggName) {
        final int pos = aggName.indexOf(':');
        final String type = pos >= 0 ? aggName.substring(0, pos) : "DOCUNIT";
        final String field = pos >= 0 ? aggName.substring(pos + 1) : aggName;

        switch (type) {
            case "RECORD_PROPERTY":
                // docunit > bib_record > properties [type = field] > value.raw
                final ChildrenBuilder recordAgg = new ChildrenBuilder(aggName).childType(BibliographicRecord.ES_TYPE);
                final NestedBuilder propertyAgg = new NestedBuilder("nested-property").path("properties");
                final FilterAggregationBuilder filterByTypeAgg =
                    new FilterAggregationBuilder("filter-type").filter(QueryBuilders.termsQuery("properties.type.identifier", field));
                final TermsBuilder valueAgg = new TermsBuilder(aggName).field("properties.value.raw").size(20).order(Terms.Order.count(false));

                return recordAgg.subAggregation(propertyAgg.subAggregation(filterByTypeAgg.subAggregation(valueAgg)));

            case "DOCUNIT":
            default:
                return new TermsBuilder(aggName).field(field).size(20).order(Terms.Order.count(false));
        }
    }

    private void addDocUnitSearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        switch (field) {
            case "pgcnId":
                builder.should(getFullTextQueryBuilder(DocUnit_.pgcnId.getName(), search, false)); // pas de recherche fuzzy sur le pgcnId
                break;
            case "label":
                builder.should(getFullTextQueryBuilder(DocUnit_.label.getName(), search, fuzzy));
                break;
            case "type":
                builder.should(getExactQueryBuilder(DocUnit_.type.getName(), search));
                break;
            case "collectionIA":
                builder.should(getExactQueryBuilder(path(DocUnit_.collectionIA, InternetArchiveCollection_.identifier), search));
                break;
            case "planClassementPAC":
                builder.should(getExactQueryBuilder(path(DocUnit_.planClassementPAC, CinesPAC_.identifier), search));
                break;
            case "archivable":
                builder.should(getExactQueryBuilder(DocUnit_.archivable.getName(), Boolean.parseBoolean(search)));
                break;
            case "distributable":
                builder.should(getExactQueryBuilder(DocUnit_.distributable.getName(), Boolean.parseBoolean(search)));
                break;
            case "rights":
                builder.should(getExactQueryBuilder(DocUnit_.rights.getName(), search));
                break;
            case "embargo":
                builder.should(getRangeQueryBuilder(DocUnit_.embargo.getName(), search));
                break;
            case "checkDelay":
                builder.should(getRangeQueryBuilder(DocUnit_.checkDelay.getName(), search));
                break;
            case "checkEndTime":
                builder.should(getRangeQueryBuilder(DocUnit_.checkEndTime.getName(), search));
                break;
            case "digitalId":
                builder.should(getExactQueryBuilder(path(DocUnit_.physicalDocuments, PhysicalDocument_.digitalId), search));
                break;
            case "library":
                builder.should(getExactQueryBuilder(path(DocUnit_.library, Library_.identifier), search));
                break;
            case "project":
                builder.should(getExactQueryBuilder(DocUnit_.projectId.getName(), search));
                break;
            case "lot":
                builder.should(getExactQueryBuilder(DocUnit_.lotId.getName(), search));
                break;
            case "nbDigitalDocuments":
                if ("false".equals(search)) {
                    builder.should(getExactQueryBuilder("nbDigitalDocuments", "0"));
                } else {
                    builder.should(QueryBuilders.rangeQuery("nbDigitalDocuments").gt(0));
                }
                break;
            case "workflowState":
                builder.should(getExactQueryBuilder("workflowStateKeys", search));
                break;
            case "createdDate":
                builder.should(getRangeQueryBuilder(DocUnit_.createdDate.getName(), search));
                break;
            case "lastModifiedDate":
                builder.should(getRangeQueryBuilder(DocUnit_.lastModifiedDate.getName(), search));
                builder.should(QueryBuilders.hasChildQuery(BibliographicRecord.ES_TYPE,
                                                           getRangeQueryBuilder(BibliographicRecord_.lastModifiedDate.getName(), search)));
                break;
            case "latestDeliveryDate":
                builder.should(getRangeQueryBuilder("latestDeliveryDate", search));
                break;
            case "masterSize":
                builder.should(getRangeQueryBuilder("masterSize", search));
                break;
        }
    }

    private void addRecordsearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        switch (field) {
            case "title":
                builder.should(QueryBuilders.hasChildQuery(BibliographicRecord.ES_TYPE,
                                                           getFullTextQueryBuilder(BibliographicRecord_.title.getName(), search, fuzzy)));
                break;
        }
    }

    private void addPropertySearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        final BoolQueryBuilder propQuery = QueryBuilders.boolQuery()
                                                        // type
                                                        .must(getExactQueryBuilder(path(BibliographicRecord_.properties,
                                                                                        DocProperty_.type,
                                                                                        DocPropertyType_.identifier), field))
                                                        // valeur
                                                        .must(getFullTextQueryBuilder(path(BibliographicRecord_.properties, DocProperty_.value),
                                                                                      search,
                                                                                      fuzzy));
        // nested
        builder.should(QueryBuilders.hasChildQuery(BibliographicRecord.ES_TYPE,
                                                   QueryBuilders.nestedQuery(BibliographicRecord_.properties.getName(), propQuery)));
    }

    private void addPhysicalDocSearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        switch (field) {
            case "totalPage":
                builder.should(getRangeQueryBuilder(path(DocUnit_.physicalDocuments, PhysicalDocument_.totalPage), search));
                break;
        }
    }

    private void addCinesSearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        switch (field) {
            case "dateSent":
                builder.should(QueryBuilders.hasChildQuery(CinesReport.ES_TYPE, getRangeQueryBuilder(path(CinesReport_.dateSent), search)));
                break;
            case "status":
                builder.should(QueryBuilders.hasChildQuery(CinesReport.ES_TYPE, getExactQueryBuilder(path(CinesReport_.status), search)));
                break;
        }
    }

    private void addIaSearch(final EsQueryBuilder builder, final String search, final String field, final boolean fuzzy) {
        switch (field) {
            case "dateSent":
                builder.should(QueryBuilders.hasChildQuery(InternetArchiveReport.ES_TYPE,
                                                           getRangeQueryBuilder(path(InternetArchiveReport_.dateSent), search)));
                break;
            case "status":
                builder.should(QueryBuilders.hasChildQuery(InternetArchiveReport.ES_TYPE,
                                                           getExactQueryBuilder(path(InternetArchiveReport_.status), search)));
                break;
        }
    }

    private void addDefaultSearch(final EsQueryBuilder builder, final String search, final boolean fuzzy) {
        builder
            // Unité documentaire
            .should(getFullTextQueryBuilder(DocUnit_.pgcnId.getName(), search, false)) // pas de recherche fuzzy sur le pgcnId
            .should(getFullTextQueryBuilder(DocUnit_.label.getName(), search, fuzzy))
            .should(getExactQueryBuilder(DocUnit_.type.getName(), search))
            .should(getExactQueryBuilder(DocUnit_.arkUrl.getName(), search))
            .should(getExactQueryBuilder(DocUnit_.rights.getName(), search))
            .should(getExactQueryBuilder(DocUnit_.condReportType.getName(), search))
            // Record: titre
            .should(QueryBuilders.hasChildQuery(BibliographicRecord.ES_TYPE,
                                                getFullTextQueryBuilder(BibliographicRecord_.title.getName(), search, fuzzy)))
            // Record: propriétés
            .should(QueryBuilders.hasChildQuery(BibliographicRecord.ES_TYPE,
                                                QueryBuilders.nestedQuery(BibliographicRecord_.properties.getName(),
                                                                          getFullTextQueryBuilder(path(BibliographicRecord_.properties,
                                                                                                       DocProperty_.value), search, fuzzy))));
    }
}

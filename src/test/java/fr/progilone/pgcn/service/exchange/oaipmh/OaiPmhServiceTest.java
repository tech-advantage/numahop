package fr.progilone.pgcn.service.exchange.oaipmh;

import fr.progilone.pgcn.domain.jaxb.oaipmh.ListRecordsType;
import fr.progilone.pgcn.domain.jaxb.oaipmh.RecordType;
import fr.progilone.pgcn.domain.jaxb.oaipmh.ResumptionTokenType;
import org.junit.Before;
import org.junit.Test;

public class OaiPmhServiceTest {

    private static final String URL = "https://oai.datacite.org/oai";

    private OaiPmhService service;

    @Before
    public void setUp() {
        service = new OaiPmhService();
    }

    @Test
    public void testIdentify() {
        service.listRecords(URL, "oai_dc", "2018-08-01", null, "BL.EXETER", null).ifPresent(content -> {
            final ListRecordsType listRecords = content.getListRecords();

            final ResumptionTokenType token = listRecords.getResumptionToken();
            System.out.println(token.getValue());

            for (final RecordType record : listRecords.getRecord()) {
                System.out.println(record.getMetadata().getAny());

                //                ElementNSImpl
                //                OaiDcType
            }
        });
    }
}

package fr.progilone.pgcn.domain.dto.statistics;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;

import javax.annotation.Nullable;

public class StatisticsProviderDeliveryDTO implements Comparable<StatisticsProviderDeliveryDTO> {

    private static final Ordering<StatisticsProviderDeliveryDTO> ORDER_DTO;

    static {
        Ordering<StatisticsProviderDeliveryDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(StatisticsProviderDeliveryDTO::getLibraryName);
        Ordering<StatisticsProviderDeliveryDTO> orderProvider = Ordering.natural().nullsFirst().onResultOf(StatisticsProviderDeliveryDTO::getProviderFullName);
        ORDER_DTO = orderLib.compound(orderProvider);
    }

    private String libraryIdentifier;

    @CsvBindByName(column = "1. Bibliothèque")
    private String libraryName;

    private String providerIdentifier;

    @CsvBindByName(column = "2. Prestataire: login")
    private String providerLogin;

    @CsvBindByName(column = "3. Prestataire: nom")
    private String providerFullName;

    @CsvBindByName(column = "4. Nombre de lots")
    private long nbLot;

    @CsvBindByName(column = "5. Nombre de livraisons")
    private long nbDelivery;

    @CsvBindByName(column = "6. Délai moyen 1e livraison")
    private long delayFirstDelivery;

    @CsvBindByName(column = "7. Délai moyen des livraisons")
    private long delayNextDelivery;

    public String getLibraryIdentifier() {
        return libraryIdentifier;
    }

    public void setLibraryIdentifier(final String libraryIdentifier) {
        this.libraryIdentifier = libraryIdentifier;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(final String libraryName) {
        this.libraryName = libraryName;
    }

    public String getProviderIdentifier() {
        return providerIdentifier;
    }

    public void setProviderIdentifier(final String providerIdentifier) {
        this.providerIdentifier = providerIdentifier;
    }

    public String getProviderLogin() {
        return providerLogin;
    }

    public void setProviderLogin(final String providerLogin) {
        this.providerLogin = providerLogin;
    }

    public String getProviderFullName() {
        return providerFullName;
    }

    public void setProviderFullName(final String providerFullName) {
        this.providerFullName = providerFullName;
    }

    public long getNbLot() {
        return nbLot;
    }

    public void setNbLot(final long nbLot) {
        this.nbLot = nbLot;
    }

    public long getNbDelivery() {
        return nbDelivery;
    }

    public void setNbDelivery(final long nbDelivery) {
        this.nbDelivery = nbDelivery;
    }

    public long getDelayFirstDelivery() {
        return delayFirstDelivery;
    }

    public void setDelayFirstDelivery(final long delayFirstDelivery) {
        this.delayFirstDelivery = delayFirstDelivery;
    }

    public long getDelayNextDelivery() {
        return delayNextDelivery;
    }

    public void setDelayNextDelivery(final long delayNextDelivery) {
        this.delayNextDelivery = delayNextDelivery;
    }

    @Override
    public int compareTo(@Nullable final StatisticsProviderDeliveryDTO o) {
        return ORDER_DTO.compare(this, o);
    }
}

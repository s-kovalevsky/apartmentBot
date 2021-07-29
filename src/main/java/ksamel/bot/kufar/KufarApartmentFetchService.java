package ksamel.bot.kufar;

import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KufarApartmentFetchService implements ApartmentFetchService {
    private final static Logger logger = LoggerFactory.getLogger(KufarApartmentFetchService.class);
    private static final String NAME = "kufar";
    private static final String URL = "https://cre-api.kufar.by/items-search/v1/engine/v1/search/rendered-paginated";
    private static final String globalParams = "prn=1000&size=200&sort=lst.d&typ=let&cat=1040&cur=USD&rnl=3&gtsy=country-belarus~province-minsk~locality-minsk";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Apartment> getApartments(ApartmentFilter apartmentFilter) throws IOException {
        List<Apartment> apartments = new ArrayList<>();
        try {
            String params = globalParams;
            List<String> paramsList = new ArrayList<>();
            if (apartmentFilter.getPriceFrom() != null && apartmentFilter.getPriceTo() != null) {
                params += "&prc=r%3A" + apartmentFilter.getPriceFrom() + "%2C" + apartmentFilter.getPriceTo();
            } else if (apartmentFilter.getPriceFrom() != null) {
                params += "&prc=r%3A" + apartmentFilter.getPriceFrom() + "%2C1000000000";
            } else if (apartmentFilter.getPriceTo() != null) {
                params += "&prc=r%3A0%2C" + apartmentFilter.getPriceTo();
            }
            KufarResponceModel responceModel = Utils.doGet(URL + "?" + params,
                    KufarResponceModel.class);

            Stream<KufarApartmentModel> apartmentModelStream = responceModel.getAds().stream()
                    .filter(a -> !a.getCompanyAd());
            if (apartmentFilter.getUpdatedFrom() != null) {
                apartmentModelStream = apartmentModelStream
                        .filter(x -> !apartmentFilter.getUpdatedFrom().after(x.getListTime()));

            }
            apartments = apartmentModelStream.map(this::toApartment).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(NAME + " error: " + e.getMessage());
            throw e;
        }
        return apartments;
    }

    public Apartment toApartment(KufarApartmentModel apartmentModel) {
        String address = "";
        if (apartmentModel.getAccountParameters() != null) {
            Optional<KufarApartmentModel.AccountParameters> address1 = apartmentModel.getAccountParameters().stream()
                    .filter(x -> x.getP().equals("address"))
                    .findFirst();
            if (address1.isPresent()) {
                address = address1.get().getV();
            }
        }
        return new Apartment(apartmentModel.getPriceUsd() / 100, NAME, apartmentModel.getId(),
                apartmentModel.getLink(), apartmentModel.getListTime(),
                address);
    }

}

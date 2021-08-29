package ksamel.bot.kufar;

import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KufarApartmentFetchService implements ApartmentFetchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KufarApartmentFetchService.class);
    private static final String NAME = "kufar";
    private static final String URL = "https://cre-api.kufar.by/items-search/v1/engine/v1/search/rendered-paginated";
    private static final String PARAMS = "prn=1000&size=200&sort=lst.d&typ=let&cat=1040&cur=USD&rnl=3&gtsy=country-belarus~province-minsk~locality-minsk";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Apartment> getApartments(Integer priceFrom, Integer priceTo, Date updatedFrom) throws IOException {
        String params = PARAMS;
        if (priceFrom != null && priceTo != null) {
            params += "&prc=r%3A" + priceFrom + "%2C" + priceTo;
        } else if (priceFrom != null) {
            params += "&prc=r%3A" + priceFrom + "%2C1000000000";
        } else if (priceTo != null) {
            params += "&prc=r%3A0%2C" + priceTo;
        }
        KufarResponceModel responceModel = Utils.doGetRequest(URL + "?" + params,
                KufarResponceModel.class);

        Stream<KufarApartmentModel> apartmentModelStream = responceModel.getAds().stream()
                .filter(a -> !a.getCompanyAd());
        if (updatedFrom != null) {
            apartmentModelStream = apartmentModelStream
                    .filter(x -> !updatedFrom.after(x.getListTime()));
        }
        return apartmentModelStream.map(this::toApartment).collect(Collectors.toList());
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

package ksamel.bot.kufar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.core.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KufarApartmentFetchService implements ApartmentFetchService {

    private static final String NAME = "kufar";
    private static final String URL = "https://api.kufar.by/search-api/v2/search/rendered-paginated";
    private static final String globalParams = "cat=1010&cmp=0&cur=USD&gtsy=country-belarus~province-minsk~locality-minsk&lang=ru&rms=v.or%3A1&rnt=1&size=30&typ=let";

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
            if (apartmentFilter.getPriceUsdFrom() != null && apartmentFilter.getPriceUsdTo() != null) {
                params += "&prc=r%3A" + apartmentFilter.getPriceUsdFrom() + "%2C" + apartmentFilter.getPriceUsdTo();
            } else if (apartmentFilter.getPriceUsdFrom() != null) {
                params += "&prc=r%3A" + apartmentFilter.getPriceUsdFrom() + "%2C1000000000";
            } else if (apartmentFilter.getPriceUsdTo() != null) {
                params += "&prc=r%3A0%2C" + apartmentFilter.getPriceUsdTo();
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
            log.error(NAME + " error: " + e.getMessage());
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

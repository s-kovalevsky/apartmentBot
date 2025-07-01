package ksamel.bot.kufar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.core.Utils;
import ksamel.bot.kufar.KufarApartmentModel.Parameters;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KufarApartmentFetchService implements ApartmentFetchService {

    private static final String NAME = "kufar";
    private static final String URL = "https://api.kufar.by/search-api/v2/search/rendered-paginated";
    private static final String URL_PREPOSITION = "https://api.kufar.by/search-api/v2/search/poleposition";
    private static final String globalParams = "cat=1010&cmp=0&cur=USD&gtsy=country-belarus~province-minsk~locality-minsk&lang=ru&rms=v.or%3A1&rnt=1&size=5&typ=let";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Apartment> getApartments(ApartmentFilter apartmentFilter) throws IOException {
        List<Apartment> apartments;
        try {
            String params = globalParams;
            if (apartmentFilter.getPriceUsdFrom() != null && apartmentFilter.getPriceUsdTo() != null) {
                params += "&prc=r%3A" + apartmentFilter.getPriceUsdFrom() + "%2C" + apartmentFilter.getPriceUsdTo();
            } else if (apartmentFilter.getPriceUsdFrom() != null) {
                params += "&prc=r%3A" + apartmentFilter.getPriceUsdFrom() + "%2C1000000000";
            } else if (apartmentFilter.getPriceUsdTo() != null) {
                params += "&prc=r%3A0%2C" + apartmentFilter.getPriceUsdTo();
            }
            KufarResponceModel responceModel = Utils.doGet(URL + "?" + params,
                                                           KufarResponceModel.class);
            KufarResponceModel responceModelPrepositin = Utils.doGet(URL_PREPOSITION + "?" + params,
                                                                     KufarResponceModel.class);

            List<KufarApartmentModel> apartmentModelStream = new ArrayList<>();
            apartmentModelStream.addAll(responceModel.getAds());
            apartmentModelStream.addAll(responceModelPrepositin.getAds());
            apartments = apartmentModelStream.stream()
                                             .map(this::toApartment)
                                             .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(NAME + " error: {}", e.getMessage());
            throw e;
        }
        return apartments;
    }

    public Apartment toApartment(KufarApartmentModel apartmentModel) {
        String address = "";
        Double longitude = null;
        Double latitude = null;
        if (apartmentModel.getAccountParameters() != null) {
            Optional<Parameters> address1 = apartmentModel.getAccountParameters().stream()
                                                          .filter(x -> x.getP().equals("address"))
                                                          .findFirst();
            if (address1.isPresent()) {
                address = String.valueOf(address1.get().getV());
            }
            Optional<Object> coordinates = apartmentModel.getAdParameters()
                                                         .stream()
                                                         .filter(parameters -> parameters.getP().equals("coordinates"))
                                                         .map(Parameters::getV)
                                                         .findFirst();
            if (coordinates.isPresent()) {
                Object coordinateObject = coordinates.get();
                if (coordinateObject instanceof Iterable<?> iterable) {
                    Iterator<?> iterator = iterable.iterator();
                    longitude = Double.valueOf(String.valueOf(iterator.next()));
                    latitude = Double.valueOf(String.valueOf(iterator.next()));
                }
            }

        }
        return new Apartment(apartmentModel.getPriceUsd() / 100,
                             NAME,
                             apartmentModel.getId(),
                             apartmentModel.getLink(),
                             apartmentModel.getListTime(),
                             address,
                             longitude,
                             latitude);
    }
}

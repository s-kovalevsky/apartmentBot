package ksamel.bot.onliner;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.core.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OnlinerApartmentFetchService implements ApartmentFetchService {

    private static final String NAME = "onliner";
    public static final String URL = "https://r.onliner.by/sdapi/ak.api/search/apartments";

    @Override
    public List<Apartment> getApartments(ApartmentFilter apartmentFilter) throws IOException {
        List<Apartment> apartments;
        try {
            List<String> paramsList = new ArrayList<>();
            if (apartmentFilter.getPriceUsdFrom() != null) {
                paramsList.add("price[min]=" + apartmentFilter.getPriceUsdFrom());
            }
            if (apartmentFilter.getPriceUsdTo() != null) {
                paramsList.add("price[max]=" + apartmentFilter.getPriceUsdTo());
            }
            paramsList.add("currency=usd&rent_type[]=1_room&rent_type[]=2_rooms&only_owner=true&order=last_time_up:desc");
            paramsList.add("bounds[lb][lat]=53.672307307332225&bounds[lb][long]=27.402334948196245&bounds[rt][lat]=54.208239284622316&bounds[rt][long]=27.809515734329057");
            paramsList.add("v=0.12507621617511955");

            OnlinerResponseModel onlinerResponseModel;
            List<OnlinerApartmentModel> onlinerApartments = new ArrayList<>();
            int currentPage = 1;
            String params = "?" + String.join("&", paramsList);
            while (true) {
                onlinerResponseModel = Utils.doGet(URL + params + "&page=" + currentPage,
                                                   OnlinerResponseModel.class);
                onlinerApartments.addAll(onlinerResponseModel.getApartments());
                if (onlinerResponseModel.getPage().getLast() == currentPage ||
                    apartmentFilter.getUpdatedFrom().after(onlinerApartments.get(onlinerApartments.size() - 1).getLastTimeUp())) {
                    break;
                }
                currentPage++;
            }
            Stream<OnlinerApartmentModel> onlinerApartmentModelStream = onlinerApartments.stream()
                                                                                         .filter(a -> a.getContact().getOwner());
            if (apartmentFilter.getUpdatedFrom() != null) {
                onlinerApartmentModelStream = onlinerApartmentModelStream
                        .filter(x -> !apartmentFilter.getUpdatedFrom().after(x.getLastTimeUp()));

            }
            apartments = onlinerApartmentModelStream.map(this::toApartment).collect(toList());
        } catch (Exception e) {
            log.error(NAME + " error: " + e.getMessage());
            throw e;
        }
        return apartments;
    }

    public Apartment toApartment(OnlinerApartmentModel onlinerApartmentModel) {
        return new Apartment(onlinerApartmentModel.getPrice().getAmount(),
                             NAME,
                             onlinerApartmentModel.getId(),
                             onlinerApartmentModel.getUrl(),
                             onlinerApartmentModel.getLastTimeUp(),
                             onlinerApartmentModel.getLocation().getAddress(),
                             onlinerApartmentModel.getLocation().getLongitude(),
                             onlinerApartmentModel.getLocation().getLatitude());
    }

    public String getName() {
        return NAME;
    }
}

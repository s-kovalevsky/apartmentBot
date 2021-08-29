package ksamel.bot.onliner;

import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OnlinerApartmentFetchService implements ApartmentFetchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OnlinerApartmentFetchService.class);
    private static final String NAME = "onliner";
    public static final String URL = "https://r.onliner.by/sdapi/ak.api/search/apartments";

    @Override
    public List<Apartment> getApartments(Integer priceFrom, Integer priceTo, Date updatedFrom) throws IOException {
        List<String> paramsList = new ArrayList<>();
        if (priceFrom != null) {
            paramsList.add("price[min]=" + priceFrom);
        }
        if (priceTo != null) {
            paramsList.add("price[max]=" + priceTo);
        }
        paramsList.add("currency=usd&rent_type[0]=room&order=last_time_up:desc");

        OnlinerResponseModel onlinerResponseModel;
        List<OnlinerApartmentModel> onlinerApartments = new ArrayList<>();
        int currentPage = 1;
        String params = "?" + String.join("&", paramsList);
        while (true) {
            onlinerResponseModel = Utils.doGetRequest(URL + params + "&page=" + currentPage,
                    OnlinerResponseModel.class);
            onlinerApartments.addAll(onlinerResponseModel.getApartments());
            if (onlinerResponseModel.getPage().getLast() == currentPage ||
                    updatedFrom.after(onlinerApartments.get(onlinerApartments.size() - 1).getLastTimeUp())) {
                break;
            }
            currentPage++;
        }
        Stream<OnlinerApartmentModel> onlinerApartmentModelStream = onlinerApartments.stream()
                .filter(a -> a.getContact().getOwner());
        if (updatedFrom != null) {
            onlinerApartmentModelStream = onlinerApartmentModelStream
                    .filter(x -> !updatedFrom.after(x.getLastTimeUp()));

        }
        return onlinerApartmentModelStream.map(this::toApartment).collect(Collectors.toList());
    }

    public Apartment toApartment(OnlinerApartmentModel onlinerApartmentModel) {
        return new Apartment(onlinerApartmentModel.getPrice().getAmount(), NAME, onlinerApartmentModel.getId(), onlinerApartmentModel.getUrl(), onlinerApartmentModel.getLastTimeUp(), onlinerApartmentModel.getLocation().getAddress());
    }

    public String getName() {
        return NAME;
    }
}

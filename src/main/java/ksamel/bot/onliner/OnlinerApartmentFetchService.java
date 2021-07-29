package ksamel.bot.onliner;

import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OnlinerApartmentFetchService implements ApartmentFetchService {
    private final static Logger logger = LoggerFactory.getLogger(OnlinerApartmentFetchService.class);
    private final static String NAME = "onliner";
    public final static String URL = "https://r.onliner.by/sdapi/ak.api/search/apartments";

    @Override
    public List<Apartment> getApartments(ApartmentFilter apartmentFilter) throws IOException {
        List<Apartment> apartments = new ArrayList<>();
        try {
            List<String> paramsList = new ArrayList<>();
            if (apartmentFilter.getPriceFrom() != null) {
                paramsList.add("price[min]=" + apartmentFilter.getPriceFrom());
            }
            if (apartmentFilter.getPriceTo() != null) {
                paramsList.add("price[max]=" + apartmentFilter.getPriceTo());
            }
            paramsList.add("currency=usd&rent_type[0]=room&order=last_time_up:desc");
//            OnlinerResponseModel onlinerResponseModel = HttpUtils.doGet(URL + "?" + String.join("&", paramsList),
//                    OnlinerResponseModel.class);

            OnlinerResponseModel onlinerResponseModel;
            List<OnlinerApartmentModel> onlinerApartments = new ArrayList<>();
            int currentPage = 1;
            String params = "?" + String.join("&", paramsList);
            while (true){
                onlinerResponseModel = Utils.doGet(URL + params + "&page=" + currentPage,
                        OnlinerResponseModel.class);
                onlinerApartments.addAll(onlinerResponseModel.getApartments());
                if (onlinerResponseModel.getPage().getLast() == currentPage ||
                        apartmentFilter.getUpdatedFrom().after(onlinerApartments.get(onlinerApartments.size() - 1).getLastTimeUp())){
                    break;
                }
                currentPage++;
            }
            Stream<OnlinerApartmentModel> onlinerApartmentModelStream = onlinerApartments.stream()
                    .filter(a -> a.getContact().getOwner());
            if (apartmentFilter.getUpdatedFrom() != null){
                onlinerApartmentModelStream = onlinerApartmentModelStream
                        .filter(x->!apartmentFilter.getUpdatedFrom().after(x.getLastTimeUp()));

            }
            apartments = onlinerApartmentModelStream.map(this::toApartment).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(NAME + " error: " + e.getMessage());
            throw e;
        }
        return apartments;
    }

    public Apartment toApartment(OnlinerApartmentModel onlinerApartmentModel) {
        return new Apartment(onlinerApartmentModel.getPrice().getAmount(), NAME, onlinerApartmentModel.getId(), onlinerApartmentModel.getUrl(), onlinerApartmentModel.getLastTimeUp(), onlinerApartmentModel.getLocation().getAddress());
    }

    public String getName() {
        return NAME;
    }
}

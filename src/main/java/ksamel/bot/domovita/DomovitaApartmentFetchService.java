package ksamel.bot.domovita;

import static java.lang.String.format;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.ApartmentFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.select.Evaluator;

public class DomovitaApartmentFetchService implements ApartmentFetchService {

    private static final String NAME = "Domovita";
    private static final String URL_FORMAT = "https://domovita.by/minsk/flats/rent?rooms=1,2&price[min]=%s&price[max]=%s&individual=yes&price_type=all_usd&order=-date_revision&ajax=1";
    private static final Gson GSON = new Gson();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Apartment> getApartments(ApartmentFilter apartmentFilter) throws IOException {
        String url = format(URL_FORMAT, apartmentFilter.getPriceUsdFrom(), apartmentFilter.getPriceUsdTo());
        Document doc = Jsoup.connect(url).get();
        return doc.select(new Evaluator.Tag("script"))
                  .stream()
                  .filter(element -> element.attr("type").equals("application/ld+json"))
                  .map(element -> element.childNode(0))
                  .filter(node -> node.nodeName().equals("#data") && node instanceof DataNode)
                  .map(n -> ((DataNode) n).getWholeData())
                  .filter(s -> s.contains("Apartment"))
                  .map(data -> GSON.fromJson(data, ksamel.bot.domovita.model.Apartment.class))
                  .map(this::builApartment)
                  .toList();
    }

    private Apartment builApartment(ksamel.bot.domovita.model.Apartment apartment) {
        return Apartment.builder()
                        .source(getName())
                        .link(apartment.getUrl())
                        .address(apartment.getAddress().getStreetAddress())
                        .longitude(apartment.getGeo().getLongitude())
                        .latitude(apartment.getGeo().getLatitude())
                        .build();
    }
}

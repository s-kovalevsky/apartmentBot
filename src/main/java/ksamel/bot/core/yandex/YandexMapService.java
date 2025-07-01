package ksamel.bot.core.yandex;

import static ksamel.bot.core.Utils.doGet;

import java.util.Collection;
import java.util.List;
import ksamel.bot.core.yandex.model.Component;
import ksamel.bot.core.yandex.model.FeatureMember;
import ksamel.bot.core.yandex.model.GeoCodeResponse;

public class YandexMapService {

    public static final YandexMapService INSTANCE = new YandexMapService();
    private final String API_KEY = "9d06d295-a78a-4968-bc99-ff71dacb34e8";
    private final String DISTRICT = "district";
    private final String LOCALITY = "locality";
    private final String REQUEST_FORMAT = "https://geocode-maps.yandex.ru/1.x?apikey=9d06d295-a78a-4968-bc99-ff71dacb34e8&lang=ru_RU&kind=%s&geocode=%s,%s&format=json&results=%s";

    public List<String> getDistricts(Double lon, Double lat) {
        GeoCodeResponse geoCodeResponse = getGeoCodeResponse(DISTRICT, lon, lat, 10);
        return retrieveLocationNames(geoCodeResponse, DISTRICT);
    }

    public List<String> getLocalities(Double lon, Double lat) {
        GeoCodeResponse geoCodeResponse = getGeoCodeResponse(LOCALITY, lon, lat, 1);
        return retrieveLocationNames(geoCodeResponse, LOCALITY);
    }

    private GeoCodeResponse getGeoCodeResponse(String kind, Double lon, Double lat, int limit) {
        String url = String.format(REQUEST_FORMAT, kind, lon, lat, limit);
        return doGet(url, GeoCodeResponse.class);
    }

    private List<String> retrieveLocationNames(GeoCodeResponse response, String kind) {
        return response.getResponse()
                       .getGeoObjectCollection()
                       .getFeatureMembers()
                       .stream()
                       .map(FeatureMember::getGeoObject)
                       .map(geoObject -> geoObject.getMetaDataProperty().getGeocoderMetaData().getAddress().getComponents())
                       .flatMap(Collection::stream)
                       .filter(component -> kind.equals(component.getKind()))
                       .map(Component::getName)
                       .toList();
    }
}

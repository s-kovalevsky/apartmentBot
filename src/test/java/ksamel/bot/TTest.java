package ksamel.bot;

import java.io.IOException;
import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.core.yandex.YandexMapService;
import ksamel.bot.domovita.DomovitaApartmentFetchService;
import ksamel.bot.realt.RealtApartmentFeatchService;
import org.junit.jupiter.api.Test;

public class TTest {

//    @Test
    void test() {
        YandexMapService yandexMapService = new YandexMapService();
        var r = yandexMapService.getLocalities(27.614500,53.933213);
        System.out.println(r);
    }

//    @Test
    void realtTest() {
        RealtApartmentFeatchService realtApartmentFeatchService = new RealtApartmentFeatchService();
        ApartmentFilter filter = new ApartmentFilter();
        filter.setPriceUsdFrom(150);
        filter.setPriceUsdTo(320);
        var r = realtApartmentFeatchService.getApartments(filter);
        System.out.println(r);
    }

//    @Test
    void domovitaTest() throws IOException {
        DomovitaApartmentFetchService domovitaApartmentFetchService = new DomovitaApartmentFetchService();
        ApartmentFilter filter = new ApartmentFilter();
        filter.setPriceUsdFrom(150);
        filter.setPriceUsdTo(320);
        var a = domovitaApartmentFetchService.getApartments(filter);
        System.out.println(a);
    }
}

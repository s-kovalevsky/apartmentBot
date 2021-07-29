package ksamel.bot;

import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.onliner.OnlinerApartmentFetchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class OnlinerApartmentFetchServiceTest {
    Logger logger = LoggerFactory.getLogger(OnlinerApartmentFetchServiceTest.class);

    OnlinerApartmentFetchService onlinerApartmentService;
    OnlinerApartmentFetchServiceTest(){
        this.onlinerApartmentService = new OnlinerApartmentFetchService();
    }

    @Test
    public void getApartmentsTest() throws IOException {
        ApartmentFilter apartmentFilter = new ApartmentFilter(80, 120, Date.from(Instant.now().minus(Duration.ofDays(1))));
        Assertions.assertNotNull(onlinerApartmentService.getApartments(apartmentFilter));
    }

}

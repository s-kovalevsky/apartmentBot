package ksamel.bot;

import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.kufar.KufarApartmentFetchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class KufarApartmentFetchServiceTest {
    private final KufarApartmentFetchService kufarApartmentFetchService;

    public KufarApartmentFetchServiceTest() {
        this.kufarApartmentFetchService = new KufarApartmentFetchService();
    }

    @Test
    public void getApartmentsTest() throws IOException {
        ApartmentFilter apartmentFilter = new ApartmentFilter(80, 120, Date.from(Instant.now().minus(Duration.ofDays(1))));
        Assertions.assertNotNull(kufarApartmentFetchService.getApartments(apartmentFilter));
    }

}

package ksamel.bot.core;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ApartmentFetchService {
    String getName();
    List<Apartment> getApartments(Integer priceFrom, Integer priceTo, Date updatedFrom) throws IOException;
}

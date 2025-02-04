package ksamel.bot.core;

import java.io.IOException;
import java.util.List;

public interface ApartmentFetchService {

    String getName();

    List<Apartment> getApartments(ApartmentFilter apartmentFilter) throws IOException;
}

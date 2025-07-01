package ksamel.bot.telegram;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.union;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.core.yandex.YandexMapService;
import ksamel.bot.domovita.DomovitaApartmentFetchService;
import ksamel.bot.kufar.KufarApartmentFetchService;
import ksamel.bot.onliner.OnlinerApartmentFetchService;
import ksamel.bot.realt.RealtApartmentFeatchService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class UserHandler {

    private final ApartmentFilter apartmentFilter;
    private final List<String> blockedLinks;
    private final Long chatId;
    private final AbsSender absSender;
    private final List<ApartmentFetchService> apartmentFetchServices;

    private int period;
    private TimeUnit timeUnit;

    private final ScheduledExecutorService scheduler = newScheduledThreadPool(1);
    private ScheduledFuture<?> future;

    private final Map<Apartment, Date> apartmentsToUpdatedAtMap = new HashMap<>();
    private boolean justStarted = true;

    private Map<String, List<Date>> filterNameToErrorDateTime = new HashMap<>();
    private Map<String, Date> alertDate = new HashMap<>();

    public UserHandler(ApartmentFilter apartmentFilter, List<String> blockedLinks, Long chatId, AbsSender absSender, int period, TimeUnit timeUnit) {
        this.apartmentFilter = apartmentFilter;
        this.blockedLinks = blockedLinks;
        this.chatId = chatId;
        this.absSender = absSender;
        this.period = period;
        this.timeUnit = timeUnit;
        this.apartmentFetchServices = new ArrayList<>();
        this.apartmentFetchServices.add(new OnlinerApartmentFetchService());
        this.apartmentFetchServices.add(new KufarApartmentFetchService());
        this.apartmentFetchServices.add(new RealtApartmentFeatchService());
        this.apartmentFetchServices.add(new DomovitaApartmentFetchService());

    }

    public void start() {
        if (future != null && !future.isDone()) {
            return;
        }
        future = scheduler.scheduleAtFixedRate(this::doFetch, 0, period, timeUnit);
    }

    public void doFetch() {
        fetchNew().forEach(apartment -> sendAnswer(apartment.toString()));
        apartmentFilter.setUpdatedFrom(new Date());
        checkErrors();
    }

    private List<Apartment> fetchNew() {
        Map<Boolean, List<Apartment>> apartments = apartmentFetchServices.stream()
                                                                         .map(this::doFetch)
                                                                         .flatMap(Collection::stream)
                                                                         .collect(partitioningBy(this::existsInCache));
        List<Apartment> newApartments = apartments.get(false);
        List<Apartment> existingApartments = apartments.get(true);
        updateCache(newApartments);
        updateCache(existingApartments);
        removeOldApartmentsFromCache();
        if (justStarted) {
            justStarted = false;
            return emptyList();
        }
        return filter(newApartments);
    }

    private List<Apartment> filter(List<Apartment> apartments) {
        apartments = filterIsNotBlocked(apartments);
        apartments = filterByLocationIfNeed(apartments);
        return apartments;
    }

    private List<Apartment> filterIsNotBlocked(List<Apartment> apartments) {
        return apartments.stream()
                         .filter(apartment -> isNull(apartment.getLink()) || !blockedLinks.contains(apartment.getLink()))
                         .toList();
    }

    private List<Apartment> filterByLocationIfNeed(List<Apartment> apartments) {
        if (isEmpty(apartmentFilter.getLocations())) {
            return apartments;
        }
        return apartments.stream()
                         .filter(apartment -> isLocationMatchedExceptionSafe(apartment.getLongitude(),
                                                                             apartment.getLatitude(),
                                                                             apartmentFilter.getLocations()))
                         .toList();
    }

    private boolean isLocationMatchedExceptionSafe(Double longitude, Double latitude, Set<String> filterDistricts) {
        try {
            if (isNull(longitude) && isNull(latitude)) {
                return true;
            }
            return isLocationMatched(longitude, latitude, filterDistricts);
        } catch (NullPointerException e) {
            log.error("NullPointerException", e);
            return true;
        }
    }

    private boolean isLocationMatched(Double longitude, Double latitude, Set<String> filterDistricts) {
        List<String> districts = YandexMapService.INSTANCE.getDistricts(longitude, latitude);
        List<String> localities = YandexMapService.INSTANCE.getLocalities(longitude, latitude);
        Collection<String> locations = union(districts, localities);
        if (isEmpty(locations)) {
            return true;
        }
        return locations.stream()
                        .anyMatch(filterDistricts::contains);
    }


    private void updateCache(List<Apartment> apartments) {
        apartments.forEach(apartment -> apartmentsToUpdatedAtMap.put(apartment, new Date()));
    }

    private void removeOldApartmentsFromCache() {
        Date expiredAt = new Date(System.currentTimeMillis() - timeUnit.toMillis(period * 10));
        List<Apartment> expiredApartments = apartmentsToUpdatedAtMap.entrySet()
                                                                    .stream()
                                                                    .filter(entry -> entry.getValue().before(expiredAt))
                                                                    .map(Map.Entry::getKey)
                                                                    .toList();
        expiredApartments.forEach(apartmentsToUpdatedAtMap::remove);
    }

    private boolean existsInCache(Apartment apartment) {
        return apartmentsToUpdatedAtMap.containsKey(apartment);
    }

    private List<Apartment> doFetch(ApartmentFetchService service) {
        try {
            List<Apartment> apartments = service.getApartments(apartmentFilter);
            return apartments;
        } catch (Exception e) {
            log.error("{} error: {}", service.getName(), e.getMessage(), e);
            var dates = filterNameToErrorDateTime.getOrDefault(service.getName(), new ArrayList<>());
            dates.add(new Date());
            filterNameToErrorDateTime.put(service.getName(), dates);
            return emptyList();
        }
    }

    public void stop() {
        if (future == null || future.isCancelled()) {
            return;
        }
        future.cancel(true);
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
        if (future != null && !future.isDone()) {
            stop();
            start();
        }
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        if (future != null && !future.isDone()) {
            stop();
            start();
        }
    }

    public void sendFilterParameters() {
        sendAnswer(apartmentFilter.toString());
    }

    void sendAnswer(String text) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public ApartmentFilter getApartmentFilter() {
        return apartmentFilter;
    }

    public List<String> getBlockedLinks() {
        return blockedLinks;
    }

    public String getStatus() {
        if (future == null || future.isCancelled() || future.isDone()) {
            return "Stopped";
        }
        return "Running";
    }

    private void checkErrors() {
        if (chatId != 807873919L) {
            return;
        }
        Date errorExpiredAt = new Date(System.currentTimeMillis() - timeUnit.toMillis(period * 10));

        filterNameToErrorDateTime = filterNameToErrorDateTime.entrySet()
                                                             .stream()
                                                             .collect(toMap(Entry::getKey, entry -> entry.getValue()
                                                                                                         .stream()
                                                                                                         .filter(date -> date.after(errorExpiredAt))
                                                                                                         .toList()));
        filterNameToErrorDateTime.forEach((key, value) -> {
            if (value.size() >= 5 && !alertDate.containsKey(key)) {
                alertDate.put(key, new Date());
                sendAnswer("Error with provider " + key);
            }
            if (value.size() < 2 && alertDate.containsKey(key)) {
                alertDate.remove(key);
                sendAnswer("Resolved error with provider " + key);
            }
        });

        for (String key: alertDate.keySet()) {
            if (alertDate.get(key).before(errorExpiredAt)) {
                alertDate.put(key, new Date());
                sendAnswer("Error with provider " + key + " still exists");
            }
        }
    }
}

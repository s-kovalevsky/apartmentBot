package ksamel.bot.telegram;

import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.kufar.KufarApartmentFetchService;
import ksamel.bot.onliner.OnlinerApartmentFetchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

public class UserHandler {
    private final static Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private final ApartmentFilter apartmentFilter;
    private final List<String> blockedLinks;
    private final Long chatId;
    private final AbsSender absSender;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int period;
    private TimeUnit timeUnit;
    private ScheduledFuture<?> future;
    private List<ApartmentFetchService> apartmentFetchServices;

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

    }

    public void start() {
        if (future != null && !future.isDone()) {
            return;
        }
        future = scheduler.scheduleAtFixedRate(this::doFetch, 0, period, timeUnit);
    }

    public void doFetch(){
        Set<Apartment> apartments = new LinkedHashSet<>();
        for (ApartmentFetchService service : apartmentFetchServices) {
            try {
                apartments.addAll(service.getApartments(apartmentFilter));
            } catch (Exception e) {
                logger.error(service.getName() + " error: " + e.getMessage());
                sendAnswer(service.getName() + " error: " + e.getMessage());
            }
        }
        for (Apartment apartment : apartments) {
            if (blockedLinks.contains(apartment.getLink())){
                continue;
            }
            sendAnswer(apartment.toString());
        }
        apartmentFilter.setUpdatedFrom(new Date());
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

    public String getStatus(){
        if (future == null || future.isCancelled() || future.isDone()){
            return "Stopped";
        }
        return "Running";
    }
}

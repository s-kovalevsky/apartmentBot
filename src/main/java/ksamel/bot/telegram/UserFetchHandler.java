package ksamel.bot.telegram;

import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
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
import java.util.stream.Collectors;

public class UserFetchHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserFetchHandler.class);
    private Integer priceFrom;
    private Integer priceTo;
    private Date updatedFrom;
    private List<String> blockedLinks;
    private int period;
    private TimeUnit timeUnit;

    private final Long chatId;
    private final AbsSender absSender;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> future;
    private final List<ApartmentFetchService> apartmentFetchServices;

    public UserFetchHandler(List<String> blockedLinks, Long chatId, AbsSender absSender, int period, TimeUnit timeUnit) {
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

    public void doFetch() {
        List<Apartment> apartments = new ArrayList<>();
        updatedFrom = new Date();
        for (ApartmentFetchService service : apartmentFetchServices) {
            try {
                apartments.addAll(service.getApartments(priceFrom, priceTo, updatedFrom));
            } catch (Exception e) {
                String msg = String.format("Service: %s, exception: %s", service.getName(), e);
                LOGGER.error(msg);
                sendAnswer(msg);
            }
        }
        apartments = apartments.stream()
                .filter(a -> !blockedLinks.contains(a.getLink()))
                .sorted(Comparator.comparing(Apartment::getUpdateDate))
                .collect(Collectors.toList());
        for (Apartment apartment : apartments) {
            sendAnswer(apartment.toString());
        }
        updatedFrom = new Date();
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

    public List<String> getBlockedLinks() {
        return blockedLinks;
    }

    public String getStatus() {
        if (future == null || future.isCancelled() || future.isDone()) {
            return "Stopped";
        }
        return "Running";
    }
}

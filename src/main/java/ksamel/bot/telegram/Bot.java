package ksamel.bot.telegram;

import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.telegram.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class Bot extends TelegramLongPollingCommandBot {
    private Logger logger = LoggerFactory.getLogger(Bot.class);

    private static Integer defaultPriceFrom = 80;
    private static Integer defaultPriceTo = 140;
    private static int defaultPeriod = 5;
    private static TimeUnit defaultTimeUnit = TimeUnit.MINUTES;

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    /**
     * Ключ - уникальный id чата
     */
    private static Map<Long, UserHandler> tasks;

    public Bot(String botName, String botToken) {
        super();
        logger.debug("Конструктор суперкласса отработал");
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        logger.debug("Имя и токен присвоены");

        register(new StartCommand("start", "Старт"));
        logger.debug("Команда start создана");

        register(new ParamsCommand("/params", "Параметры"));
        logger.debug("Команда params создана");

        register(new FetchCommand("/fetch", "Проверить"));
        logger.debug("Команда doFetch создана");

        register(new StopCommand("/stop", "Остоновить"));
        logger.debug("Команда stop создана");

        register(new StatusCommand("/status", "Статус"));
        logger.debug("Команда status создана");

        logger.info("Бот создан!");
        tasks = new HashMap<>();
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }


    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
    }

    public static Map<Long, UserHandler> getHandlers() {
        return tasks;
    }

    public static void setTasks(Map<Long, UserHandler> tasks) {
        Bot.tasks = tasks;
    }

    public static ApartmentFilter getDefaultApartmentFilter() {
        return new ApartmentFilter(defaultPriceFrom, defaultPriceTo, new Date());
    }

    public static int getDefaultPeriod() {
        return defaultPeriod;
    }

    public static TimeUnit getDefaultTimeUnit() {
        return defaultTimeUnit;
    }

    public static UserHandler getOrCreateHandler(Chat chat, AbsSender absSender) {
        UserHandler handler = Bot.getHandlers().get(chat.getId());
        if (handler == null){
            handler = new UserHandler(Bot.getDefaultApartmentFilter(),
                    new ArrayList<>(),
                    chat.getId(),
                    absSender,
                    Bot.getDefaultPeriod(),
                    Bot.getDefaultTimeUnit());
            Bot.getHandlers().put(chat.getId(), handler);
        }
        return handler;
    }
}

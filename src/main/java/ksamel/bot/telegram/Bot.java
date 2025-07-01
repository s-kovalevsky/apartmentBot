package ksamel.bot.telegram;

import static java.util.List.of;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.telegram.commands.FetchCommand;
import ksamel.bot.telegram.commands.ParamsCommand;
import ksamel.bot.telegram.commands.StartCommand;
import ksamel.bot.telegram.commands.StatusCommand;
import ksamel.bot.telegram.commands.StopCommand;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
public final class Bot extends TelegramLongPollingCommandBot {

    public static Integer defaultPriceFrom = 150;
    public static Integer defaultPriceTo = 350;
    public static int defaultPeriod = 1;
    public static TimeUnit defaultTimeUnit = TimeUnit.MINUTES;
    public static Set<String> defaultDistricts = Set.of("Центральный район",
                                                        "Советский район",
                                                        "Первомайский район",
                                                        "Партизанский район",
                                                        "деревня Боровляны",
                                                        "агрогородок Лесной",
                                                        "деревня Копище");

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    private static Map<Long, UserHandler> tasks;

    public Bot(String botName, String botToken) {
        super();
        log.debug("Конструктор суперкласса отработал");
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        log.debug("Имя и токен присвоены");

        register(new StartCommand("start", "Старт"));
        log.debug("Команда start создана");

        register(new ParamsCommand("/params", "Параметры"));
        log.debug("Команда params создана");

        register(new FetchCommand("/fetch", "Проверить"));
        log.debug("Команда doFetch создана");

        register(new StopCommand("/stop", "Остоновить"));
        log.debug("Команда stop создана");

        register(new StatusCommand("/status", "Статус"));
        log.debug("Команда status создана");

        log.info("Бот создан!");
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

    public static ApartmentFilter getDefaultApartmentFilter() {
        return new ApartmentFilter(defaultPriceFrom, defaultPriceTo, new Date(), defaultDistricts);
    }

    public void setupDefaultTasks() {
        of(807873919L, 675083518L).forEach(this::setupDefaultTask);
    }

    public void setupDefaultTask(Long chatId) {
        var handler = getOrCreateHandler(chatId, this);
        handler.start();
        handler.sendAnswer("Bot was restarted");
        handler.sendFilterParameters();
    }

    public static UserHandler getOrCreateHandler(Chat chat, AbsSender absSender) {
        // 807873919
        // 675083518
        log.info(chat.getId() + " " + chat.getUserName());
        return getOrCreateHandler(chat.getId(), absSender);
    }

    public static UserHandler getOrCreateHandler(Long chatId, AbsSender absSender) {
        UserHandler handler = Bot.getHandlers().get(chatId);
        if (handler == null) {
            handler = new UserHandler(Bot.getDefaultApartmentFilter(),
                                      new ArrayList<>(),
                                      chatId,
                                      absSender,
                                      Bot.defaultPeriod,
                                      Bot.defaultTimeUnit);
            Bot.getHandlers().put(chatId, handler);
        }
        return handler;
    }
}

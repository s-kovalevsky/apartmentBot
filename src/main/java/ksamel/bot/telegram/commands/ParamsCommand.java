package ksamel.bot.telegram.commands;

import ksamel.bot.core.Utils;
import ksamel.bot.telegram.Bot;
import ksamel.bot.telegram.UserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class ParamsCommand extends ServiceCommand {
    private Logger logger = LoggerFactory.getLogger(StartCommand.class);

    public ParamsCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);
        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));
        UserHandler userHandler = Bot.getOrCreateHandler(chat, absSender);
        if (strings.length == 0) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                    String.format("*Текущие настройки*\n" +
                                    "(-pf) price from - *%s*\n" +
                                    "(-pt) price to - *%s*\n" +
                                    "(-d)(format dd.MM.yyyy&HH:mm) lastDateUp - *%s*\n" +
                                    "(-pr) period - *%s*\n" +
                                    "(-tu)(%s) timeUnit - *%s*\n" +
                                    "(-b) blockedLinks - *%s*\n",
                            userHandler.getApartmentFilter().getPriceFrom().toString(),
                            userHandler.getApartmentFilter().getPriceTo().toString(),
                            new SimpleDateFormat("dd.MM.yyyy&HH:mm").format(userHandler.getApartmentFilter().getUpdatedFrom()),
                            userHandler.getPeriod(),
                            Arrays.stream(ChronoUnit.values()).skip(3).limit(5).map(ChronoUnit::toString).collect(Collectors.joining(", ")),
                            userHandler.getTimeUnit().toString(),
                            String.join(", ", userHandler.getBlockedLinks())
                    )
            );
        }
        else {
            try {
                List<String> strings1 = List.of(strings);
                if (strings1.contains("-pf")) {
                    Integer priceFrom = Integer.valueOf(strings1.get(strings1.indexOf("-pf") + 1));
                    userHandler.getApartmentFilter().setPriceFrom(priceFrom);
                }
                if (strings1.contains("-pt")) {
                    Integer priceTo = Integer.valueOf(strings1.get(strings1.indexOf("-pt") + 1));
                    userHandler.getApartmentFilter().setPriceTo(priceTo);
                }
                if (strings1.contains("-d")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy&HH:mm");
                    Date d = simpleDateFormat.parse(strings1.get(strings1.indexOf("-d") + 1));
                    userHandler.getApartmentFilter().setUpdatedFrom(d);
                }
                if (strings1.contains("-pr")) {
                    int period = Integer.parseInt(strings1.get(strings1.indexOf("-pr") + 1));
                    userHandler.setPeriod(period);
                }
                if (strings1.contains("-tu")) {
                    TimeUnit timeUnit = TimeUnit.valueOf(strings1.get(strings1.indexOf("-tu") + 1));
                    userHandler.setTimeUnit(timeUnit);
                }
                if (strings1.contains("-b")) {
                    String s = strings1.get(strings1.indexOf("-b") + 1);
                    userHandler.getBlockedLinks().add(s);
                }
            }
            catch (Exception e){
                logger.debug("error " + e.getMessage());
            }

        }
        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}

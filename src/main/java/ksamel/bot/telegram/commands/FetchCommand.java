package ksamel.bot.telegram.commands;

import ksamel.bot.core.Utils;
import ksamel.bot.telegram.Bot;
import ksamel.bot.telegram.UserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;


public class FetchCommand extends ServiceCommand {
    private Logger logger = LoggerFactory.getLogger(FetchCommand.class);

    public FetchCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        UserHandler task = Bot.getOrCreateHandler(chat, absSender);
        String userName = Utils.getUserName(user);
        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));
        task.doFetch();
        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}

package ksamel.bot.telegram.commands;

import ksamel.bot.core.Utils;
import ksamel.bot.telegram.Bot;
import ksamel.bot.telegram.UserFetchHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;


public class StopCommand extends ServiceCommand {
    private Logger logger = LoggerFactory.getLogger(StopCommand.class);

    public StopCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        UserFetchHandler handler = Bot.getOrCreateHandler(chat, absSender);
        String userName = Utils.getUserName(user);

        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));
        handler.stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            logger.error(e.getMessage());
        }
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Status " + handler.getStatus());
        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}

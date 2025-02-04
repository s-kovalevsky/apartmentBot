package ksamel.bot.telegram.commands;

import ksamel.bot.core.Utils;
import ksamel.bot.telegram.Bot;
import ksamel.bot.telegram.UserHandler;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
public class StopCommand extends ServiceCommand {

    public StopCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        UserHandler handler = Bot.getOrCreateHandler(chat, absSender);
        String userName = Utils.getUserName(user);

        log.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                                this.getCommandIdentifier()));
        handler.stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                   "Status " + handler.getStatus());
        log.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                                this.getCommandIdentifier()));
    }
}

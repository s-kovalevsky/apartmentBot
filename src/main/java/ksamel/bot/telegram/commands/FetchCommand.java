package ksamel.bot.telegram.commands;

import ksamel.bot.core.Utils;
import ksamel.bot.telegram.Bot;
import ksamel.bot.telegram.UserHandler;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;


@Slf4j
public class FetchCommand extends ServiceCommand {

    public FetchCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        UserHandler task = Bot.getOrCreateHandler(chat, absSender);
        String userName = Utils.getUserName(user);
        log.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                                this.getCommandIdentifier()));
        task.doFetch();
        log.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                                this.getCommandIdentifier()));
    }
}

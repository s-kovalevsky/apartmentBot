package ksamel.bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ksamel.bot.telegram.Bot;

import java.util.Map;

public class Application {
    private static final Map<String, String> getenv = System.getenv();

    public static void main(String[] args) {
        try {
//            String name = getenv.get("BOT_NAME");
//            String token = getenv.get("TELEGRAM_TOKEN");
            String name = "apartment_ksamel_bot";
            String token = "1929781185:AAHvCimL0YVXkE2-gW6zjvUM4CTo2AuPei0";
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(name, token));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

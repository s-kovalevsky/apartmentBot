package ksamel.bot;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import ksamel.bot.telegram.Bot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
public class Application {

    public static void main(String[] args) {
        try {
            String name = "apartment_ksamel_bot";
            String token = "1929781185:AAHvCimL0YVXkE2-gW6zjvUM4CTo2AuPei0";
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot(name, token);
            botsApi.registerBot(bot);
            bot.setupDefaultTasks();
            startHealthCheckEndpoint();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static void startHealthCheckEndpoint() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/health-check", new HealthCheckHandler());
            server.setExecutor(null);
            server.start();
            log.info("Health check started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class HealthCheckHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Ok";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}

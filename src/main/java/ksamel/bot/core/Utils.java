package ksamel.bot.core;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import com.google.gson.Gson;
import java.io.IOException;
import lombok.SneakyThrows;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class Utils {

    public static String getUserName(Message msg) {
        return getUserName(msg.getFrom());
    }

    public static String getUserName(User user) {
        return (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    public static String doGet(String url) throws IOException {
        final Content getResult = Request.Get(url)
                                         .setHeader("Accept", "application/json")
                                         .connectTimeout(30000)
                                         .execute().returnContent();
        return getResult.asString();
    }

    @SneakyThrows
    public static <T> T doGet(String url, Class<T> responseClass) {
        return new Gson().fromJson(doGet(url), responseClass);
    }

    @SneakyThrows
    public static <T> T doPost(String url, String body, Class<T> responseClass) {
        final Content content = Request.Post(url)
                                       .setHeader("Accept", "application/json")
                                       .bodyString(body, APPLICATION_JSON)
                                       .connectTimeout(30000)
                                       .execute()
                                       .returnContent();
        return new Gson().fromJson(content.asString(), responseClass);
    }
}

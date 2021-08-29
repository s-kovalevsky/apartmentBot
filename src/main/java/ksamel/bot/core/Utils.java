package ksamel.bot.core;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.IOException;

public class Utils {

    public static String getUserName(Message msg) {
        return getUserName(msg.getFrom());
    }

    public static String getUserName(User user) {
        return (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    public static String doGetRequest(String url) throws IOException {
        final Content getResult = Request.Get(url)
                .setHeader("Accept", "application/json")
                .execute().returnContent();
        return getResult.asString();
    }

    public static <T> T doGetRequest(String url, Class<T> responseClass) throws IOException {
        return new Gson().fromJson(doGetRequest(url), responseClass);
    }
}

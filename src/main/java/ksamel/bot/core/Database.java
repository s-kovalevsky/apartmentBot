package ksamel.bot.core;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private final ConnectionFactory connectionFactory;

    public Database(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private Connection getConnection() throws SQLException {
        return connectionFactory.getConnection();
    }

    private void doSomething(){

    }
}

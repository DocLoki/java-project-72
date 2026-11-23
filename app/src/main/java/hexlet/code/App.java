package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class App {

    private static HikariDataSource dataSource;

    public static Javalin getApp() {
        var hikariConfig = new HikariConfig();

        String databaseUrl = System.getenv().getOrDefault(
                "JDBC_DATABASE_URL",
                "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;"
        );

        hikariConfig.setJdbcUrl(databaseUrl);
        dataSource = new HikariDataSource(hikariConfig);

        try (var connection = dataSource.getConnection()) {
            String schema = readSchema();
            try (var statement = connection.createStatement()) {
                statement.execute(schema);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }

        return Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        });
    }

    private static String readSchema() throws IOException {
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (inputStream == null) {
                throw new IOException("schema.sql not found");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        getApp().start(port);
    }
}
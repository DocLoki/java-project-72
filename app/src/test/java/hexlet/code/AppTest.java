
package hexlet.code;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repositories.UrlRepository;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    private io.javalin.Javalin app;
    private HikariDataSource dataSource;
    private UrlRepository urlRepository;

    private final TestConfig testConfig = new TestConfig(
            true,
            true,
            HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build()
    );

    @BeforeEach
    void setUp() {
        String databaseUrl = "jdbc:h2:mem:test_" + UUID.randomUUID()
                + ";DB_CLOSE_DELAY=-1";

        app = App.getApp(databaseUrl);
        dataSource = App.getDataSource();
        urlRepository = new UrlRepository(dataSource);
    }

    @AfterEach
    void tearDown() {
        if (app != null) {
            app.stop();
        }

        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    void testIndex() {
        JavalinTest.test(app, testConfig, (server, client) -> {
            var response = client.get("/");
            String body = response.body().string();

            assertEquals(200, response.code());
            assertTrue(body.contains(
                    "\u0410\u043d\u0430\u043b\u0438\u0437\u0430\u0442\u043e\u0440 "
                            + "\u0441\u0442\u0440\u0430\u043d\u0438\u0446"
            ));
        });
    }

    @Test
    void testCreateUrl() throws Exception {
        JavalinTest.test(app, testConfig, (server, client) -> {
            var response = client.post(
                    "/urls",
                    "url=https://example.com/some/path?test=1"
            );
            String body = response.body().string();

            assertEquals(200, response.code());
            assertTrue(body.contains("https://example.com"));
            assertTrue(body.contains("data-test=\"url\""));

            var urls = urlRepository.findAll();

            assertEquals(1, urls.size());
            assertEquals("https://example.com", urls.get(0).getName());

            var detailResponse = client.get("/urls/" + urls.get(0).getId());
            String detailBody = detailResponse.body().string();

            assertEquals(200, detailResponse.code());
            assertTrue(detailBody.contains("https://example.com"));
            assertTrue(detailBody.contains("data-test=\"url\""));
        });
    }

    @Test
    void testCreateDuplicateUrl() throws Exception {
        var existingUrl = urlRepository.save("https://example.com");

        JavalinTest.test(app, testConfig, (server, client) -> {
            var response = client.post(
                    "/urls",
                    "url=https://example.com/some/path"
            );
            String body = response.body().string();

            assertEquals(200, response.code());
            assertTrue(body.contains("https://example.com"));
            assertTrue(body.contains("data-test=\"url\""));

            var urls = urlRepository.findAll();

            assertEquals(1, urls.size());
            assertEquals("https://example.com", urls.get(0).getName());

            var detailResponse = client.get("/urls/" + existingUrl.getId());
            String detailBody = detailResponse.body().string();

            assertEquals(200, detailResponse.code());
            assertTrue(detailBody.contains("https://example.com"));
            assertTrue(detailBody.contains("data-test=\"url\""));
        });
    }

    @Test
    void testCreateInvalidUrl() {
        JavalinTest.test(app, testConfig, (server, client) -> {
            var response = client.post("/urls", "url=not-a-url");
            String body = response.body().string();

            assertEquals(422, response.code());
            assertTrue(body.contains(
                    "\u041d\u0435\u043a\u043e\u0440\u0440\u0435\u043a\u0442\u043d\u044b\u0439 URL"
            ));

            assertTrue(urlRepository.findAll().isEmpty());
        });
    }

    @Test
    void testListUrls() throws Exception {
        urlRepository.save("https://example.com");

        JavalinTest.test(app, testConfig, (server, client) -> {
            var response = client.get("/urls");
            String body = response.body().string();

            assertEquals(200, response.code());
            assertTrue(body.contains("https://example.com"));
            assertTrue(body.contains("data-test=\"urls\""));
        });
    }

    @Test
    void testShowUrl() throws Exception {
        var url = urlRepository.save("https://example.com");

        JavalinTest.test(app, testConfig, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            String body = response.body().string();

            assertEquals(200, response.code());
            assertTrue(body.contains("https://example.com"));
            assertTrue(body.contains("data-test=\"url\""));
        });
    }

    @Test
    void testShowNonExistingUrl() {
        JavalinTest.test(app, testConfig, (server, client) -> {
            var response = client.get("/urls/999");

            assertEquals(404, response.code());
        });
    }
}

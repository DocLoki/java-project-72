
package hexlet.code;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.TestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    private static MockWebServer mockWebServer;

    private io.javalin.Javalin app;
    private HikariDataSource dataSource;
    private UrlRepository urlRepository;
    private UrlCheckRepository urlCheckRepository;

    private final TestConfig testConfig = new TestConfig(
            true,
            true,
            HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build()
    );

    @BeforeAll
    static void startMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopMockServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        String databaseUrl = "jdbc:h2:mem:test_" + UUID.randomUUID()
                + ";DB_CLOSE_DELAY=-1";

        app = App.getApp(databaseUrl);
        dataSource = App.getDataSource();
        urlRepository = new UrlRepository(dataSource);
        urlCheckRepository = new UrlCheckRepository(dataSource);
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
            assertTrue(body.contains("Анализатор страниц"));
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
            assertTrue(body.contains("Некорректный URL"));

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

    @Test
    void testSuccessfulUrlCheck() throws Exception {
        String html = """
                <html>
                    <head>
                        <title>Test title</title>
                        <meta name="description" content="Test description">
                    </head>
                    <body>
                        <h1>Test heading</h1>
                    </body>
                </html>
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "text/html; charset=utf-8")
                .setBody(html));

        var url = urlRepository.save(mockWebServer.url("/").toString());

        JavalinTest.test(app, testConfig, (server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks");

            assertEquals(200, response.code());

            var checks = urlCheckRepository.findByUrlId(url.getId());

            assertEquals(1, checks.size());
            assertEquals(200, checks.get(0).getStatusCode());
            assertEquals("Test title", checks.get(0).getTitle());
            assertEquals("Test heading", checks.get(0).getH1());
            assertEquals("Test description", checks.get(0).getDescription());

            var detailResponse = client.get("/urls/" + url.getId());
            String detailBody = detailResponse.body().string();

            assertEquals(200, detailResponse.code());
            assertTrue(detailBody.contains(String.valueOf(checks.get(0).getId())));
            assertTrue(detailBody.contains("200"));

            var listResponse = client.get("/urls");
            String listBody = listResponse.body().string();

            assertEquals(200, listResponse.code());
            assertTrue(listBody.contains("200"));
        });
    }

    @Test
    void testFailedUrlCheck() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        var url = urlRepository.save(mockWebServer.url("/").toString());

        JavalinTest.test(app, testConfig, (server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks");

            assertEquals(200, response.code());

            var checks = urlCheckRepository.findByUrlId(url.getId());

            assertTrue(checks.isEmpty());
        });
    }
}

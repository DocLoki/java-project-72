
package hexlet.code.controllers;

import hexlet.code.models.Url;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import hexlet.code.services.UrlCheckService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UrlController {

    private final UrlRepository urlRepository;
    private final UrlCheckRepository urlCheckRepository;
    private final UrlCheckService urlCheckService;

    public UrlController(
            UrlRepository urlRepository,
            UrlCheckRepository urlCheckRepository,
            UrlCheckService urlCheckService
    ) {
        this.urlRepository = urlRepository;
        this.urlCheckRepository = urlCheckRepository;
        this.urlCheckService = urlCheckService;
    }

    public void register(JavalinConfig config) {
        config.routes.get("/", this::index);
        config.routes.post("/urls", this::create);
        config.routes.get("/urls", this::list);
        config.routes.get("/urls/{id}", this::show);
        config.routes.post("/urls/{id}/checks", this::check);
    }

    private void index(Context ctx) {
        String flash = ctx.sessionAttribute("flash");
        ctx.sessionAttribute("flash", null);

        if (flash == null) {
            ctx.render("index.jte");
        } else {
            ctx.render("index.jte", Map.of("flash", flash));
        }
    }

    private void create(Context ctx) throws SQLException {
        String input = ctx.formParam("url");
        URL url;

        try {
            url = new URI(input).toURL();
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            showInvalidUrl(ctx);
            return;
        }

        if (url.getHost() == null || url.getHost().isBlank()
                || (!url.getProtocol().equals("http")
                && !url.getProtocol().equals("https"))) {
            showInvalidUrl(ctx);
            return;
        }

        String name = url.getProtocol() + "://" + url.getHost();

        if (url.getPort() != -1) {
            name += ":" + url.getPort();
        }

        var existingUrl = urlRepository.findByName(name).orElse(null);

        if (existingUrl != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.redirect("/urls/" + existingUrl.getId());
            return;
        }

        var savedUrl = urlRepository.save(name);

        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.redirect("/urls/" + savedUrl.getId());
    }

    private void showInvalidUrl(Context ctx) {
        ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
        ctx.render("index.jte", Map.of("flash", "Некорректный URL"));
    }

    private void list(Context ctx) throws SQLException {
        var urls = urlRepository.findAll();
        ctx.render("urls/index.jte", Map.of("urls", urls));
    }

    private void show(Context ctx) throws SQLException {
        long id = parseId(ctx);
        Url url = urlRepository.findById(id).orElseThrow(NotFoundResponse::new);

        var checks = urlCheckRepository.findByUrlId(id);

        String flash = ctx.sessionAttribute("flash");
        ctx.sessionAttribute("flash", null);

        Map<String, Object> model = new HashMap<>();
        model.put("url", url);
        model.put("checks", checks);

        if (flash != null) {
            model.put("flash", flash);
        }

        ctx.render("urls/show.jte", model);
    }

    private void check(Context ctx) throws SQLException {
        long id = parseId(ctx);
        Url url = urlRepository.findById(id).orElseThrow(NotFoundResponse::new);

        UrlCheckService.CheckResult result;

        try {
            result = urlCheckService.check(url.getName());
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
            ctx.redirect("/urls/" + id);
            return;
        }

        urlCheckRepository.save(
                id,
                result.statusCode(),
                result.title(),
                result.h1(),
                result.description()
        );

        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.redirect("/urls/" + id);
    }

    private long parseId(Context ctx) {
        try {
            return Long.parseLong(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            throw new NotFoundResponse();
        }
    }
}

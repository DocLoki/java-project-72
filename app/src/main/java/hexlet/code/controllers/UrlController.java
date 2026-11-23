package hexlet.code.controllers;

import hexlet.code.models.Url;
import hexlet.code.repositories.UrlRepository;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UrlController {

    private final UrlRepository urlRepository;

    public UrlController(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public void register(JavalinConfig config) {
        config.routes.get("/", this::index);
        config.routes.post("/urls", this::create);
        config.routes.get("/urls", this::list);
        config.routes.get("/urls/{id}", this::show);
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

    private void create(Context ctx) {
        String input = ctx.formParam("url");

        try {
            URL url = new URI(input).toURL();

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

            var existingUrl = urlRepository.findByName(name);

            if (existingUrl != null) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.redirect("/urls/" + existingUrl.getId());
                return;
            }

            var savedUrl = urlRepository.save(name);

            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.redirect("/urls/" + savedUrl.getId());

        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            showInvalidUrl(ctx);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showInvalidUrl(Context ctx) {
        ctx.status(422);
        ctx.render("index.jte", Map.of("flash", "Некорректный URL"));
    }

    private void list(Context ctx) {
        try {
            var urls = urlRepository.findAll();
            ctx.render("urls/index.jte", Map.of("urls", urls));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void show(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            Url url = urlRepository.findById(id);

            if (url == null) {
                ctx.status(404);
                return;
            }

            String flash = ctx.sessionAttribute("flash");
            ctx.sessionAttribute("flash", null);

            Map<String, Object> model = new HashMap<>();
            model.put("url", url);

            if (flash != null) {
                model.put("flash", flash);
            }

            ctx.render("urls/show.jte", model);

        } catch (NumberFormatException e) {
            ctx.status(404);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


package hexlet.code.services;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;

public class UrlCheckService {

    public CheckResult check(String url) {
        HttpResponse<String> response = Unirest.get(url)
                .connectTimeout(5000)
                .socketTimeout(10000)
                .asString();

        int statusCode = response.getStatus();

        if (statusCode < 200 || statusCode >= 400) {
            throw new IllegalStateException("Unexpected HTTP status: " + statusCode);
        }

        var document = Jsoup.parse(response.getBody());

        String title = document.title();

        var h1Element = document.selectFirst("h1");
        String h1 = h1Element != null ? h1Element.text() : null;

        var descriptionElement = document.selectFirst("meta[name=description]");
        String description = descriptionElement != null
                ? descriptionElement.attr("content") : null;

        return new CheckResult(statusCode, title, h1, description);
    }

    public record CheckResult(
            int statusCode,
            String title,
            String h1,
            String description
    ) {
    }
}

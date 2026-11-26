
package hexlet.code.models;

import java.time.LocalDateTime;

public record UrlWithLastCheck(
        Url url,
        Integer lastCheckStatusCode,
        LocalDateTime lastCheckCreatedAt
) {

    public long getId() {
        return url.getId();
    }

    public String getName() {
        return url.getName();
    }
}

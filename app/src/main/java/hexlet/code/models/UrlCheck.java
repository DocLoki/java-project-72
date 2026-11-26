
package hexlet.code.models;

import java.time.LocalDateTime;

public class UrlCheck {

    private long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private long urlId;
    private LocalDateTime createdAt;

    public UrlCheck(long id, int statusCode, String title, String h1,
                    String description, long urlId) {
        this.id = id;
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.urlId = urlId;
    }

    public UrlCheck(long id, int statusCode, String title, String h1,
                    String description, long urlId, LocalDateTime createdAt) {
        this(id, statusCode, title, h1, description, urlId);
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getTitle() {
        return title;
    }

    public String getH1() {
        return h1;
    }

    public String getDescription() {
        return description;
    }

    public long getUrlId() {
        return urlId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

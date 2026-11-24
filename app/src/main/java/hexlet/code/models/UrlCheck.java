
package hexlet.code.models;

import java.sql.Timestamp;

public class UrlCheck {

    private long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private long urlId;
    private Timestamp createdAt;

    public UrlCheck(long id, int statusCode, String title, String h1,
                    String description, long urlId, Timestamp createdAt) {
        this.id = id;
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.urlId = urlId;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}

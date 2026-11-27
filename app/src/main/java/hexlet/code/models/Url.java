
package hexlet.code.models;

import java.time.LocalDateTime;

public class Url {

    private long id;
    private String name;
    private LocalDateTime createdAt;

    public Url(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}


package hexlet.code.models;

import java.time.LocalDateTime;

public class Url {

    private long id;
    private String name;
    private LocalDateTime createdAt;

    public Url(long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
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
}

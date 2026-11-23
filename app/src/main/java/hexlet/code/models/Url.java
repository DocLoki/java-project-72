package hexlet.code.models;

import java.sql.Timestamp;

public class Url {

    private long id;
    private String name;
    private Timestamp createdAt;

    public Url(long id, String name, Timestamp createdAt) {
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
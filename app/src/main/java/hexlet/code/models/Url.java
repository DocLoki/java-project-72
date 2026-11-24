
package hexlet.code.models;

import java.sql.Timestamp;

public class Url {

    private long id;
    private String name;
    private Timestamp createdAt;
    private Integer lastCheckStatusCode;
    private Timestamp lastCheckCreatedAt;

    public Url(long id, String name, Timestamp createdAt) {
        this(id, name, createdAt, null, null);
    }

    public Url(long id, String name, Timestamp createdAt,
               Integer lastCheckStatusCode, Timestamp lastCheckCreatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.lastCheckStatusCode = lastCheckStatusCode;
        this.lastCheckCreatedAt = lastCheckCreatedAt;
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

    public Integer getLastCheckStatusCode() {
        return lastCheckStatusCode;
    }

    public Timestamp getLastCheckCreatedAt() {
        return lastCheckCreatedAt;
    }
}

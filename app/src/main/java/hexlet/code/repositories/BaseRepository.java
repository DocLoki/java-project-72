package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;

public abstract class BaseRepository {

    protected final HikariDataSource dataSource;

    protected BaseRepository(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }
}

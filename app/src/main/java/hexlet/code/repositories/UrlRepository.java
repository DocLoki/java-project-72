
package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.models.Url;
import hexlet.code.models.UrlWithLastCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public UrlRepository(HikariDataSource dataSource) {
        super(dataSource);
    }

    public Optional<Url> findById(long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (var resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapRow(resultSet))
                        : Optional.empty();
            }
        }
    }

    public Optional<Url> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            try (var resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapRow(resultSet))
                        : Optional.empty();
            }
        }
    }

    public List<UrlWithLastCheck> findAll() throws SQLException {
        String sql = """
                SELECT
                    u.id,
                    u.name,
                    u.created_at,
                    c.status_code AS last_check_status_code,
                    c.created_at AS last_check_created_at
                FROM urls u
                LEFT JOIN url_checks c ON c.id = (
                    SELECT MAX(id)
                    FROM url_checks
                    WHERE url_id = u.id
                )
                ORDER BY u.created_at DESC
                """;

        List<UrlWithLastCheck> urls = new ArrayList<>();

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                urls.add(mapRowWithLastCheck(resultSet));
            }
        }

        return urls;
    }

    public Url save(String name) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return findById(generatedKeys.getLong(1))
                            .orElseThrow(() -> new SQLException("Saved URL not found"));
                }
            }
        }

        throw new SQLException("Failed to save URL");
    }

    private Url mapRow(ResultSet resultSet) throws SQLException {
        return new Url(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("created_at").toLocalDateTime()
        );
    }

    private UrlWithLastCheck mapRowWithLastCheck(ResultSet resultSet) throws SQLException {
        Url url = mapRow(resultSet);
        Integer statusCode = (Integer) resultSet.getObject("last_check_status_code");

        var lastCheckTimestamp = resultSet.getTimestamp("last_check_created_at");

        return new UrlWithLastCheck(
                url,
                statusCode,
                lastCheckTimestamp != null ? lastCheckTimestamp.toLocalDateTime() : null
        );
    }
}

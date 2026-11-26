
package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.models.UrlCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UrlCheckRepository extends BaseRepository {

    public UrlCheckRepository(HikariDataSource dataSource) {
        super(dataSource);
    }

    public UrlCheck save(long urlId, int statusCode, String title,
                         String h1, String description) throws SQLException {
        String sql = """
                INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at)
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(
                     sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, urlId);
            statement.setInt(2, statusCode);
            statement.setString(3, title);
            statement.setString(4, h1);
            statement.setString(5, description);

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    var id = generatedKeys.getLong(1);
                    var check = new UrlCheck(
                            id,
                            statusCode,
                            title,
                            h1,
                            description,
                            urlId
                    );
                    check.setCreatedAt(findCreatedAt(id).toLocalDateTime());
                    return check;
                }
            }
        }

        throw new SQLException("Failed to save URL check");
    }

    public List<UrlCheck> findByUrlId(long urlId) throws SQLException {
        String sql = """
                SELECT * FROM url_checks
                WHERE url_id = ?
                ORDER BY created_at DESC
                """;

        List<UrlCheck> checks = new ArrayList<>();

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, urlId);

            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    checks.add(mapRow(resultSet));
                }
            }
        }

        return checks;
    }

    private UrlCheck mapRow(ResultSet resultSet) throws SQLException {
        return new UrlCheck(
                resultSet.getLong("id"),
                resultSet.getInt("status_code"),
                resultSet.getString("title"),
                resultSet.getString("h1"),
                resultSet.getString("description"),
                resultSet.getLong("url_id"),
                resultSet.getTimestamp("created_at").toLocalDateTime()
        );
    }

    private java.sql.Timestamp findCreatedAt(long id) throws SQLException {
        String sql = "SELECT created_at FROM url_checks WHERE id = ?";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getTimestamp("created_at");
                }
            }
        }

        throw new SQLException("Failed to find URL check");
    }
}

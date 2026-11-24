
package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.models.Url;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UrlRepository extends BaseRepository {

    public UrlRepository(HikariDataSource dataSource) {
        super(dataSource);
    }

    public Url findById(long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    public Url findByName(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    public List<Url> findAll() throws SQLException {
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

        List<Url> urls = new ArrayList<>();

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
                    return findById(generatedKeys.getLong(1));
                }
            }
        }

        throw new SQLException("Failed to save URL");
    }

    private Url mapRow(ResultSet resultSet) throws SQLException {
        return new Url(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("created_at")
        );
    }

    private Url mapRowWithLastCheck(ResultSet resultSet) throws SQLException {
        Integer statusCode = (Integer) resultSet.getObject("last_check_status_code");

        return new Url(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("created_at"),
                statusCode,
                resultSet.getTimestamp("last_check_created_at")
        );
    }
}

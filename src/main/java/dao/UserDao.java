package dao;




import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * {@link User}
 */
public class UserDao {
    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates table for users unless if it does not exist.
     */
    public void createTableIfAbsent() {
        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `users`(" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " name VARCHAR(100)" +
                    ")");

        } catch (SQLException e) {
            throw new UserDaoException(e);
        }
    }

    /**
     * Loads all users from the database.
     *
     * @return all found users.
     * @throws UserDaoException If any SQL-related failure happens here.
     */
    public Collection<User> getAll() {
        try (Statement statement = connection.createStatement()) {

            Collection<User> result = new ArrayList<>();

            try (ResultSet cursor = statement.executeQuery(
                    "SELECT * FROM `users`")) {
                while (cursor.next()) {
                    result.add(createUserFromCursor(cursor));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new UserDaoException(e);
        }
    }

    public Optional<User> getById(int id) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM `users` WHERE id=?")) {
            statement.setInt(1, id);

            User result;

            try (ResultSet cursor = statement.executeQuery()) {
                if (cursor.next()) {
                    result = createUserFromCursor(cursor);
                } else {
                    result = null;
                }
            }

            return Optional.ofNullable(result);
        } catch (SQLException e) {
            throw new UserDaoException(e);
        }
    }

    private User createUserFromCursor(ResultSet cursor) throws SQLException {
        int id = cursor.getInt("id");
        String name = cursor.getString("name");
        return new User (name, id);
    }

    public void insert(User user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO `users`(`name`) VALUES(?)")) {
            statement.setString(1, user.name);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.id = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new UserDaoException(e);
        }
    }

    public void update(User user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE `users` SET name=? WHERE id=?")) {
            statement.setString(1, user.name);
            statement.setInt(2, user.id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new UserDaoException(e);
        }
    }

    public void delete(User user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM `users` WHERE id=?")) {
            statement.setInt(1, user.id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new UserDaoException(e);
        }
    }

    public Collection<User> findByPartialName(String text) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM `users` WHERE `name` LIKE ?")) {
            statement.setString(1, "%" + text + "%");

            Collection<User> result = new ArrayList<>();

            try (ResultSet cursor = statement.executeQuery()) {
                while (cursor.next()) {
                    result.add(createUserFromCursor(cursor));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new UserDaoException(e);
        }
    }

    public static class UserDaoException extends RuntimeException {
        public UserDaoException(Throwable cause) {
            super(cause);
        }
    }
}

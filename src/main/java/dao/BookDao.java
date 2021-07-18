package dao;



import models.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * {@link Book}
 */
public class BookDao {
    private final Connection connection;

    public BookDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates table for books unless if it does not exist.
     */
    public void createTableIfAbsent() {
        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `books`(" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " title VARCHAR(100)," +
                    " publish_year INTEGER," +
                    " author_id INTEGER," +
                    " FOREIGN KEY (`author_id`) REFERENCES `authors`(`id`)" +
                    ")");

        } catch (SQLException e) {
            throw new BookDaoException(e);
        }
    }

    /**
     * Loads all books from the database.
     *
     * @return all found books.
     * @throws BookDaoException If any SQL-related failure happens here.
     */
    public Collection<Book> getAll() {
        try (Statement statement = connection.createStatement()) {

            Collection<Book> result = new ArrayList<>();

            try (ResultSet cursor = statement.executeQuery(
                    "SELECT * FROM `books`")) {
                while (cursor.next()) {
                    result.add(createBookFromCursor(cursor));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new BookDaoException(e);
        }
    }


    /**
     * Loads all books from the database.
     *
     * @return all found books.
     * @throws BookDaoException If any SQL-related failure happens here.
     */
    public Collection<Book> getAllByAuthorId(int authorId) {
        try (Statement statement = connection.createStatement()) {

            Collection<Book> result = new ArrayList<>();

            try (ResultSet cursor = statement.executeQuery(
                    "SELECT * FROM `books` WHERE `author_id` = " + authorId)) {
                while (cursor.next()) {
                    result.add(createBookFromCursor(cursor));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new BookDaoException(e);
        }
    }


    public Optional<Book> getById(int id) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM `books` WHERE id=?")) {
            statement.setInt(1, id);

            Book result;

            try (ResultSet cursor = statement.executeQuery()) {
                if (cursor.next()) {
                    result = createBookFromCursor(cursor);
                } else {
                    result = null;
                }
            }

            return Optional.ofNullable(result);
        } catch (SQLException e) {
            throw new BookDaoException(e);
        }
    }

    private Book createBookFromCursor(ResultSet cursor) throws SQLException {
        int id = cursor.getInt("id");
        String title = cursor.getString("title");
        int publishYear = cursor.getInt("publish_year");
        int authorId = cursor.getInt("author_id");
        return new Book(id, title, publishYear, authorId);
    }

    public void insert(Book book) {
        if (book.authorId <= 0) {
            throw new IllegalArgumentException("Author is not stored in database yet");
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO `books`" +
                        "(`title`,`publish_year`,`author_id`)" +
                        " VALUES(?, ?, ?)")) {
            statement.setString(1, book.title);
            statement.setInt(2, book.publishYear);
            statement.setInt(3, book.authorId);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.id = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new BookDaoException(e);
        }
    }

    public void update(Book book) {
        if (book.authorId <= 0) {
            throw new IllegalArgumentException("Author is not stored in database yet");
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE `books`" +
                        " SET" +
                        " `title`=?," +
                        " `publish_year`=?," +
                        " `author_id`=?" +
                        " WHERE `id`=?")) {
            statement.setString(1, book.title);
            statement.setInt(2, book.publishYear);
            statement.setInt(3, book.authorId);
            statement.setInt(4, book.id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BookDaoException(e);
        }
    }

    public void delete(Book book) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM `books` WHERE id=?")) {
            statement.setInt(1, book.id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BookDaoException(e);
        }
    }

    public Collection<Book> findByPartialName(String text) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM `books` WHERE `title` LIKE ?")) {
            statement.setString(1, "%" + text + "%");

            Collection<Book> result = new ArrayList<>();

            try (ResultSet cursor = statement.executeQuery()) {
                while (cursor.next()) {
                    result.add(createBookFromCursor(cursor));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new BookDaoException(e);
        }
    }

    public static class BookDaoException extends RuntimeException {
        public BookDaoException(Throwable cause) {
            super(cause);
        }
    }
}

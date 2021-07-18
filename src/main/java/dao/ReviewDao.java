package dao;

import models.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


public class ReviewDao {

    private final Connection connection;

    public ReviewDao(Connection connection) {
        this.connection = connection;
    }

    public void createTable() {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS reviews (" +
                    "body VARCHAR(500), " +
                    "user_id INTEGER, " +
                    "reviewId INTEGER, " +
                    "rank INTEGER, " +
                    "book_id INTEGER, " +
                    "FOREIGN KEY (user_id) REFERENCES `users`(id), " +
                    "FOREIGN KEY (book_id) REFERENCES `books`(id)" +
                    ")";
            statement.execute(sql);
        } catch (SQLException e) {
            throw new ReviewDaoException(e);
        }
    }

    public Collection<Review> getAll() {
        try (Statement statement = connection.createStatement()) {
            String sql = "SELECT * FROM reviews";
            Collection<Review> result = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    result.add(createReviewFromCursor(resultSet));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new ReviewDaoException(e);
        }
    }

    public Optional<Review> getById(int id) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM `reviews` WHERE reviewId=?")) {
            statement.setInt(1, id);
            Review result;
            try (ResultSet cursor = statement.executeQuery()) {
                if (cursor.next()) {
                    result = createReviewFromCursor(cursor);
                } else {
                    result = null;
                }
            }

            return Optional.ofNullable(result);
        } catch (SQLException e) {
            throw new UserDao.UserDaoException(e);
        }
    }

    public Collection<Review> getAllByBookId(int bookId) {
        try (Statement statement = connection.createStatement()) {

            Collection<Review> result = new ArrayList<>();

            try (ResultSet cursor = statement.executeQuery(
                    "SELECT * FROM `reviews` WHERE `book_id` =" + bookId)) {
                while (cursor.next()) {
                    result.add(createReviewFromCursor(cursor));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new ReviewDaoException(e);
        }
    }


    public void insert(Review review) {
        //body VARCHAR(500), userId INTEGER, reviewId INTEGER, rank INTEGER, bookId INTEGER
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO `reviews`(`body`, 'user_id', 'rank', 'book_id') VALUES(?, ?, ?, ?)")) {
            statement.setString(1, review.body);
            statement.setInt(2, review.userId);
            statement.setInt(3, review.rank);
            statement.setInt(4, review.bookId);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    review.userId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new UserDao.UserDaoException(e);
        }
    }

    public void update(Review review) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE `reviews` SET body=?, rank=? WHERE reviewId=?")) {
            statement.setString(1, review.body);
            statement.setInt(2, review.rank);
            statement.setInt(3, review.reviewId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ReviewDaoException(e);
        }
    }

    public void delete(Review review) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM `reviews` WHERE reviewId=?")) {
            statement.setInt(1, review.reviewId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ReviewDaoException(e);
        }
    }

    public Collection<Review> findByPartialBody(String text) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM `reviews` WHERE `body` LIKE ?")) {
            statement.setString(1, "%" + text + "%");

            Collection<Review> result = new ArrayList<>();

            try (ResultSet cursor = statement.executeQuery()) {
                while (cursor.next()) {
                    result.add(createReviewFromCursor(cursor));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new ReviewDaoException(e);
        }
    }


    private Review createReviewFromCursor(ResultSet set) throws SQLException {

        String body = set.getString("body");
        int userId = set.getInt("user_id");
        int reviewId = set.getInt("reviewId");
        int rank = set.getInt("rank");
        int bookId = set.getInt("book_id");

        return new Review(body, userId, reviewId, rank, bookId);
    }


    private static class ReviewDaoException extends RuntimeException {
        public ReviewDaoException(Throwable cause) {
            super(cause);

        }
    }
}
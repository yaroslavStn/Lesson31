import dao.AuthorDao;
import dao.BookDao;
import dao.ReviewDao;
import dao.UserDao;
import models.Author;
import models.Book;
import models.Review;
import models.User;
import repository.ILibraryRepository;
import repository.SqlLibraryRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite:library.db")) {
            AuthorDao authorDao = new AuthorDao(connection);
            BookDao bookDao = new BookDao(connection);
            UserDao userDao = new UserDao(connection);
            ReviewDao reviewDao = new ReviewDao(connection);
            authorDao.createTableIfAbsent();
            bookDao.createTableIfAbsent();
            userDao.createTableIfAbsent();
            reviewDao.createTable();


            if (authorDao.findByPartialName("Stroustrup").isEmpty()) {
                Author author = new Author(0, "Stroustrup");
                authorDao.insert(author);
            }
            if (authorDao.findByPartialName("Deitel").isEmpty()) {
                Author author = new Author(0, "Deitel");
                authorDao.insert(author);

                Book book = new Book(0,
                        "Programming with Java",
                        2006, author.id);
                bookDao.insert(book);
            }
            if (authorDao.findByPartialName("Horstmann").isEmpty()) {
                Author author = new Author(0, "Horstmann");
                authorDao.insert(author);

                Book book = new Book(0,
                        "Core Java. Volume 1. Fundamentals",
                        2018, author.id);
                bookDao.insert(book);

                Book book2 = new Book(0,
                        "Core Java. Volume 2. Advanced Features",
                        2018, author.id);
                bookDao.insert(book2);
            }

            userDao.insert(new User("Vasya",0));
            userDao.insert(new User("Petya",0));
            reviewDao.insert(new Review("good", 1, 5,1));
            reviewDao.insert(new Review("bad", 1, 1,2));
            //(String body, int userId, int reviewId, int rank, int bookId)
            ILibraryRepository repository = new SqlLibraryRepository(connection);

            Collection<ILibraryRepository.BookAuthorUserReview> bookAuthorUserReviews =
                    repository.getAllBooksReviews();
            for (ILibraryRepository.BookAuthorUserReview book : bookAuthorUserReviews) {
                System.out.println("Book: " + book.book.title);
                System.out.println("Author: " + book.author.name);
                System.out.println("reviews" + book.reviews.size());
                System.out.println("by " + book.users.size() + " users");
            }


        }
    }
}

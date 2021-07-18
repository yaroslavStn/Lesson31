package repository;

import dao.AuthorDao;
import dao.BookDao;
import dao.ReviewDao;
import dao.UserDao;
import models.Author;
import models.Book;
import models.Review;
import models.User;
import repository.ILibraryRepository;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class SqlLibraryRepository implements ILibraryRepository {
    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final ReviewDao reviewDao;
    private final UserDao userDao;

    public SqlLibraryRepository(Connection connection) {
        bookDao = new BookDao(connection);
        authorDao = new AuthorDao(connection);
        reviewDao = new ReviewDao(connection);
        userDao = new UserDao(connection);
    }

    @Override
    public Collection<BookAuthorUserReview> getAllBooksReviews() {
        Collection<Book> books = bookDao.getAll();

        Collection<BookAuthorUserReview> result = new ArrayList<>();

        for (Book book : books) {
            Optional<Author> author = getAuthorById(book.authorId);
            if (!author.isPresent()) {
                throw new IllegalStateException("Author is missing: " + book.authorId);
            }
            Collection <Review> reviews = reviewDao.getAllByBookId(book.id);
            Collection<User> users = new ArrayList<>();
            for (Review review : reviews) {
                Optional <User> user = userDao.getById(review.userId);
                if (!user.isPresent()) {
                    throw new IllegalStateException("User is missing: " + review.userId);
                }
                users.add(user.get());

            }

            result.add(new BookAuthorUserReview(book, author.get(),reviews,users));
        }
        return result;
    }


    @Override
    public Optional<Author> getAuthorById(int authorId) {
        return authorDao.getById(authorId);
    }

}

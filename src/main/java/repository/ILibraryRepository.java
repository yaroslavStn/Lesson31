package repository;

import models.Author;
import models.Book;
import models.Review;
import models.User;

import java.util.Collection;
import java.util.Optional;

/**
 * Abstract interface for repository of books and authors.
 */
public interface ILibraryRepository {


    Optional<Author> getAuthorById(int authorId);
    Collection <BookAuthorUserReview> getAllBooksReviews();

    class BookAuthorUserReview {
        public final Book book;
        public final Author author;
        public final Collection<Review> reviews;
        public final Collection <User> users;

        public BookAuthorUserReview(Book book, Author author, Collection<Review> reviews, Collection<User> users) {
            this.book = book;
            this.author = author;
            this.reviews = reviews;
            this.users = users;
        }
    }





}

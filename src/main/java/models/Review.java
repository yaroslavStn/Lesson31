package models;

public class Review {
    public String body;
    public int userId;
    public int reviewId;
    public int rank;
    public int bookId;

    public Review(String body, int userId, int rank, int bookId) {
        this.body = body;
        this.userId = userId;
        this.reviewId = 0;
        this.rank = rank;
        this.bookId = bookId;
    }

    public Review(String body, int userId, int reviewId, int rank, int bookId) {
        this.body = body;
        this.userId = userId;
        this.reviewId = reviewId;
        this.rank = rank;
        this.bookId = bookId;
    }
}

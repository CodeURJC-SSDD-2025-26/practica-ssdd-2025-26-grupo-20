package es.urjc.dto;

public class ReviewDTO {

    private Long id;
    private String comment;
    private int rating;
    private Long authorId;
    private String authorUsername;
    private Long restaurantId;

    public ReviewDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
}
package es.urjc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String comment;
    private int rating; 

    @ManyToOne
    private User author; 

    @ManyToOne
    private Restaurant restaurant; 

    public Review() {}

    public Review(String comment, int rating, User author, Restaurant restaurant) {
        this.comment = comment;
        this.rating = rating;
        this.author = author;
        this.restaurant = restaurant;
    }

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public boolean isRating1() { return this.rating == 1; }
    public boolean isRating2() { return this.rating == 2; }
    public boolean isRating3() { return this.rating == 3; }
    public boolean isRating4() { return this.rating == 4; }
    public boolean isRating5() { return this.rating == 5; }
}
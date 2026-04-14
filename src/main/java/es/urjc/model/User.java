package es.urjc.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

@Entity(name="USER_TABLE") 
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String encodedPassword; 
    private String bio;

    @Lob
    private Blob avatarImage;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles; 

    // RELACIÓN CON RESEÑAS: 
    // fetch EAGER para que el modal las cargue al instante.
    // Cascade ALL para que si borras al usuario, se borren sus reseñas.
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // RELACIÓN CON LISTAS FAVORITAS
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Lists> favoriteLists = new ArrayList<>();

    protected User() {}

    public User(String firstName, String lastName, String email, String username, String encodedPassword, String bio, String... roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.encodedPassword = encodedPassword;
        this.bio = bio;
        this.roles = new ArrayList<>(Arrays.asList(roles));
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEncodedPassword() { return encodedPassword; }
    public void setEncodedPassword(String encodedPassword) { this.encodedPassword = encodedPassword; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Blob getAvatarImage() { return avatarImage; }
    public void setAvatarImage(Blob avatarImage) { this.avatarImage = avatarImage; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    public List<Lists> getFavoriteLists() { return favoriteLists; }
    public void setFavoriteLists(List<Lists> favoriteLists) { this.favoriteLists = favoriteLists; }
}
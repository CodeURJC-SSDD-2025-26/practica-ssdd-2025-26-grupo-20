package es.urjc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.sql.Blob;
import java.util.*;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String municipality;
    private String specialty;
    private double averagePrice;
    private String address;
    private String phone;
    
    @Lob 
    private String description;

    @Lob
    private Blob imageFile;
    // mappedBy = "restaurant" significa: "Busca la relación en la variable 'restaurant' de la clase Review"
    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews = new ArrayList<>();

    // mappedBy = "restaurants" significa que esta es la parte "pasiva" de la relación Muchos a Muchos con las listas
    @ManyToMany(mappedBy = "restaurants")
    private List<Lists> includedInLists = new ArrayList<>();

    public Restaurant() {}

    public Restaurant(String name, String municipality, String specialty, double averagePrice, String address, String phone, String description) {
        this.name = name;
        this.municipality = municipality;
        this.specialty = specialty;
        this.averagePrice = averagePrice;
        this.address = address;
        this.phone = phone;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMunicipality() { return municipality; }
    public void setMunicipality(String municipality) { this.municipality = municipality; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public double getAveragePrice() { return averagePrice; }
    public void setAveragePrice(double averagePrice) { this.averagePrice = averagePrice; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Blob getImageFile() { return imageFile; }
    public void setImageFile(Blob imageFile) { this.imageFile = imageFile; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    public List<Lists> getIncludedInLists() { return includedInLists; }
    public void setIncludedInLists(List<Lists> includedInLists) { this.includedInLists = includedInLists; }
}
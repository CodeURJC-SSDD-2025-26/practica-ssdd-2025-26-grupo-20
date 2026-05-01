package es.urjc.dto;

import java.util.List;

public class ListsDTO {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerUsername;
    private List<Long> restaurantIds;

    public ListsDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
    public List<Long> getRestaurantIds() { return restaurantIds; }
    public void setRestaurantIds(List<Long> restaurantIds) { this.restaurantIds = restaurantIds; }
}
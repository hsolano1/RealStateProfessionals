package com.realestate.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "listings", indexes = {
    @Index(name = "idx_city", columnList = "city"),
    @Index(name = "idx_price", columnList = "price"),
    @Index(name = "idx_bedrooms", columnList = "bedrooms"),
    @Index(name = "idx_status", columnList = "status")
})
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 50)
    private String sourceId;

    @Column(nullable = false, length = 20)
    private String sourceSystem;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(nullable = false, length = 10)
    private String zip;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer bedrooms;

    @Column(nullable = false, precision = 3, scale = 1)
    private Double bathrooms;

    @Column(nullable = false)
    private Integer sqft;

    @Column(precision = 9, scale = 6)
    private Double latitude;

    @Column(precision = 9, scale = 6)
    private Double longitude;

    @Column(nullable = false)
    private LocalDate listedDate;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Listing() {}

    public Listing(String sourceId, String sourceSystem, String address, String city, String state, String zip,
                   BigDecimal price, Integer bedrooms, Double bathrooms, Integer sqft, Double latitude,
                   Double longitude, LocalDate listedDate, String status, String description) {
        this.sourceId = sourceId;
        this.sourceSystem = sourceSystem;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.price = price;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.sqft = sqft;
        this.latitude = latitude;
        this.longitude = longitude;
        this.listedDate = listedDate;
        this.status = status;
        this.description = description;
    }

    // Getters
    public String getId() { return id; }
    public String getSourceId() { return sourceId; }
    public String getSourceSystem() { return sourceSystem; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZip() { return zip; }
    public BigDecimal getPrice() { return price; }
    public Integer getBedrooms() { return bedrooms; }
    public Double getBathrooms() { return bathrooms; }
    public Integer getSqft() { return sqft; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public LocalDate getListedDate() { return listedDate; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZip(String zip) { this.zip = zip; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    public void setBathrooms(Double bathrooms) { this.bathrooms = bathrooms; }
    public void setSqft(Integer sqft) { this.sqft = sqft; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setListedDate(LocalDate listedDate) { this.listedDate = listedDate; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

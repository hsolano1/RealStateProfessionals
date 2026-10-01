package com.realestate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ListingDTO {
    public String id;
    public String address;
    public String city;
    public String state;
    public String zip;
    public BigDecimal price;
    public Integer bedrooms;
    public Double bathrooms;
    public Integer sqft;
    public Double latitude;
    public Double longitude;
    public LocalDate listedDate;
    public String status;
    public String description;
    public Double relevanceScore;

    public ListingDTO() {}

    public ListingDTO(String id, String address, String city, String state, String zip, BigDecimal price,
                      Integer bedrooms, Double bathrooms, Integer sqft, Double latitude, Double longitude,
                      LocalDate listedDate, String status, String description, Double relevanceScore) {
        this.id = id;
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
        this.relevanceScore = relevanceScore;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    public Double getBathrooms() { return bathrooms; }
    public void setBathrooms(Double bathrooms) { this.bathrooms = bathrooms; }
    public Integer getSqft() { return sqft; }
    public void setSqft(Integer sqft) { this.sqft = sqft; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public LocalDate getListedDate() { return listedDate; }
    public void setListedDate(LocalDate listedDate) { this.listedDate = listedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(Double relevanceScore) { this.relevanceScore = relevanceScore; }
}

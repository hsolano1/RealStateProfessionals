package com.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingDTO {
    private String id;
    private String address;
    private String city;
    private String state;
    private String zip;
    private BigDecimal price;
    private Integer bedrooms;
    private Double bathrooms;
    private Integer sqft;
    private Double latitude;
    private Double longitude;
    private LocalDate listedDate;
    private String status;
    private String description;
    private Double relevanceScore;
}

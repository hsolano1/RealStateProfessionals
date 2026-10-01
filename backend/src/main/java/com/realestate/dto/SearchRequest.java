package com.realestate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    @Min(value = 0, message = "Minimum bedrooms must be >= 0")
    private Integer minBedrooms;

    private String city;

    private String keyword;

    @NotNull(message = "Target budget is required")
    private BigDecimal targetBudget;

    @NotNull(message = "Page number is required")
    @Min(value = 1, message = "Page number must be >= 1")
    private Integer pageNumber;

    @NotNull(message = "Page size is required")
    @Min(value = 1, message = "Page size must be >= 1")
    private Integer pageSize;

    public void validate() {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
        if (pageSize > 1000) {
            throw new IllegalArgumentException("Page size cannot exceed 1000");
        }
        if (minBedrooms != null && minBedrooms < 0) {
            throw new IllegalArgumentException("Minimum bedrooms cannot be negative");
        }
        if (city != null && city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
    }
}

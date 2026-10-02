package com.realestate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SearchRequest {

    public BigDecimal minPrice;
    public BigDecimal maxPrice;

    @Min(value = 0, message = "Minimum bedrooms must be >= 0")
    public Integer minBedrooms;

    public String city;
    public String keyword;

    public BigDecimal targetBudget;

    @NotNull(message = "Page number is required")
    @Min(value = 1, message = "Page number must be >= 1")
    public Integer pageNumber;

    @NotNull(message = "Page size is required")
    @Min(value = 1, message = "Page size must be >= 1")
    public Integer pageSize;

    public SearchRequest() {}

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public Integer getMinBedrooms() { return minBedrooms; }
    public void setMinBedrooms(Integer minBedrooms) { this.minBedrooms = minBedrooms; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public BigDecimal getTargetBudget() { return targetBudget; }
    public void setTargetBudget(BigDecimal targetBudget) { this.targetBudget = targetBudget; }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

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

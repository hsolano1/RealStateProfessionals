package com.realestate.controller;

import com.realestate.dto.ListingDTO;
import com.realestate.dto.SearchRequest;
import com.realestate.dto.SearchResponse;
import com.realestate.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ListingController {

    private final ListingService listingService;

    @PostMapping("/listings/search")
    public ResponseEntity<SearchResponse> searchListings(@Valid @RequestBody SearchRequest request) {
        log.info("Search request received: city={}, minPrice={}, maxPrice={}, minBedrooms={}, keyword={}",
                request.getCity(), request.getMinPrice(), request.getMaxPrice(),
                request.getMinBedrooms(), request.getKeyword());

        SearchResponse response = listingService.search(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listings/{id}")
    public ResponseEntity<ListingDTO> getListingById(@PathVariable String id) {
        log.info("Get listing request for id: {}", id);

        ListingDTO listing = listingService.getListingById(id);
        if (listing == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(listing);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Real Estate Professionals - API");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SearchResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Validation error: {}", e.getMessage());
        SearchResponse response = SearchResponse.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SearchResponse> handleGlobalException(Exception e) {
        log.error("Unexpected error occurred", e);
        SearchResponse response = SearchResponse.builder()
                .success(false)
                .errorMessage("An unexpected error occurred")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

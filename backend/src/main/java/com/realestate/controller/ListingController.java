package com.realestate.controller;

import com.realestate.dto.ListingDTO;
import com.realestate.dto.SearchRequest;
import com.realestate.dto.SearchResponse;
import com.realestate.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping("/listings/search")
    public ResponseEntity<SearchResponse> searchListings(@Valid @RequestBody SearchRequest request) {
        SearchResponse response = listingService.search(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listings/{id}")
    public ResponseEntity<ListingDTO> getListingById(@PathVariable String id) {
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
        SearchResponse response = new SearchResponse();
        response.setSuccess(false);
        response.setErrorMessage(e.getMessage());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SearchResponse> handleGlobalException(Exception e) {
        SearchResponse response = new SearchResponse();
        response.setSuccess(false);
        response.setErrorMessage("An unexpected error occurred");
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

package com.realestate.service;

import com.realestate.dto.ListingDTO;
import com.realestate.dto.SearchRequest;
import com.realestate.dto.SearchResponse;
import com.realestate.entity.Listing;
import com.realestate.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;
    private final RankingService rankingService;

    public SearchResponse search(SearchRequest request) {
        try {
            request.validate();

            List<Listing> allMatches = listingRepository.searchListingsNoPage(
                request.getCity(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getMinBedrooms(),
                request.getKeyword()
            );

            Map<String, Double> scores = rankingService.calculateScores(
                allMatches,
                request.getTargetBudget(),
                request.getKeyword(),
                request.getMinBedrooms()
            );

            List<ListingDTO> rankedListings = allMatches.stream()
                    .map(listing -> convertToDTO(listing, scores.getOrDefault(listing.getId(), 0.0)))
                    .sorted(Comparator.comparingDouble(ListingDTO::getRelevanceScore).reversed())
                    .collect(Collectors.toList());

            int totalCount = rankedListings.size();
            int pageSize = request.getPageSize();
            int pageNumber = request.getPageNumber();
            int totalPages = (totalCount + pageSize - 1) / pageSize;

            if (pageNumber > totalPages && totalCount > 0) {
                return buildErrorResponse("Page number exceeds available pages");
            }

            List<ListingDTO> pageResults = rankedListings.stream()
                    .skip((long) (pageNumber - 1) * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            SearchResponse.SearchData data = new SearchResponse.SearchData();
            data.setListings(pageResults);
            data.setTotalCount((long) totalCount);
            data.setPageNumber(pageNumber);
            data.setPageSize(pageSize);
            data.setTotalPages(totalPages);

            SearchResponse response = new SearchResponse();
            response.setSuccess(true);
            response.setData(data);
            response.setTimestamp(LocalDateTime.now());
            return response;

        } catch (IllegalArgumentException e) {
            log.warn("Invalid search request: {}", e.getMessage());
            return buildErrorResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during search", e);
            return buildErrorResponse("An unexpected error occurred during search");
        }
    }

    public ListingDTO getListingById(String id) {
        return listingRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    private ListingDTO convertToDTO(Listing listing) {
        return convertToDTO(listing, null);
    }

    private ListingDTO convertToDTO(Listing listing, Double relevanceScore) {
        return ListingDTO.builder()
                .id(listing.getId())
                .address(listing.getAddress())
                .city(listing.getCity())
                .state(listing.getState())
                .zip(listing.getZip())
                .price(listing.getPrice())
                .bedrooms(listing.getBedrooms())
                .bathrooms(listing.getBathrooms())
                .sqft(listing.getSqft())
                .latitude(listing.getLatitude())
                .longitude(listing.getLongitude())
                .listedDate(listing.getListedDate())
                .status(listing.getStatus())
                .description(listing.getDescription())
                .relevanceScore(relevanceScore)
                .build();
    }

    private SearchResponse buildErrorResponse(String message) {
        SearchResponse response = new SearchResponse();
        response.setSuccess(false);
        response.setErrorMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public void seedTestData() {
        if (listingRepository.count() > 0) {
            return;
        }

        List<Listing> testListings = Arrays.asList(
            new Listing("A1", "MLS_A", "123 Main St, Apt 4B", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 2, 1.5, 980, 38.7893, -77.1873,
                java.time.LocalDate.of(2026, 8, 29), "active",
                "Bright top-floor condo near shops and transit. Pet friendly."),
            new Listing("A2", "MLS_A", "456 Oak Ave", "Springfield", "VA", "22150",
                new BigDecimal("525000"), 3, 2.0, 1450, 38.7791, -77.1901,
                java.time.LocalDate.of(2026, 9, 2), "active",
                "Updated kitchen, fenced yard, close to schools."),
            new Listing("A3", "MLS_A", "789 Pine Rd", "Fairfax", "VA", "22030",
                new BigDecimal("399000"), 2, 1.0, 850, 38.8462, -77.3064,
                java.time.LocalDate.of(2026, 9, 1), "active",
                "Cozy starter home, no pets."),
            new Listing("A4", "MLS_A", "22 Birch Ln", "Reston", "VA", "20190",
                new BigDecimal("610000"), 4, 3.0, 2100, 38.9586, -77.3570,
                java.time.LocalDate.of(2026, 9, 3), "active",
                "Spacious family home near Reston Town Center. Pets welcome."),
            new Listing("A5", "MLS_A", "55 Elm Ct", "Vienna", "VA", "22180",
                new BigDecimal("470000"), 3, 2.0, 1300, 38.9012, -77.2653,
                java.time.LocalDate.of(2026, 9, 4), "active",
                "Quiet cul-de-sac, walkable to Metro. No pets."),
            new Listing("A6", "MLS_A", "300 Cedar Blvd", "Manassas", "VA", "20110",
                new BigDecimal("415000"), 3, 2.0, 1600, 38.7509, -77.4753,
                java.time.LocalDate.of(2026, 8, 10), "active",
                "Split-level home, large driveway, pets allowed."),
            new Listing("A7", "MLS_A", "42 Willow Way", "Chantilly", "VA", "20151",
                new BigDecimal("540000"), 4, 2.5, 1950, 38.8909, -77.4316,
                java.time.LocalDate.of(2026, 7, 28), "pending",
                "Corner lot, recently painted, no pets due to HOA."),
            new Listing("B7", "MLS_B", "123 Main Street, Unit 4B", "Springfield", "VA", "22150",
                new BigDecimal("452000"), 2, 1.5, 980, 38.7893, -77.1873,
                java.time.LocalDate.of(2026, 8, 27), "active",
                "Top floor condo, walk to shopping. Pets allowed."),
            new Listing("B8", "MLS_B", "456 Oak Avenue", "Springfield", "VA", "22151",
                new BigDecimal("527500"), 3, 2.0, 1450, 38.7791, -77.1901,
                java.time.LocalDate.of(2026, 8, 30), "active",
                "Renovated kitchen, fenced backyard, near schools."),
            new Listing("B10", "MLS_B", "100 Maple Dr", "Reston", "VA", "20190",
                new BigDecimal("585000"), 3, 2.5, 1900, 38.9601, -77.3499,
                java.time.LocalDate.of(2026, 8, 20), "active",
                "Townhome with 2-car garage, community pool.")
        );

        listingRepository.saveAll(testListings);
    }
}

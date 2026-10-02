package com.realestate.service;

import com.realestate.dto.ListingDTO;
import com.realestate.dto.SearchRequest;
import com.realestate.dto.SearchResponse;
import com.realestate.entity.Listing;
import com.realestate.storage.ListingStorage;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ListingService {

    private final ListingStorage listingStorage;
    private final RankingService rankingService;

    public ListingService(ListingStorage listingStorage, RankingService rankingService) {
        this.listingStorage = listingStorage;
        this.rankingService = rankingService;
    }

    public SearchResponse search(SearchRequest request) {
        try {
            request.validate();

            List<Listing> allListings = listingStorage.findAll();
            List<Listing> filteredListings = filterListings(allListings, request);

            Map<String, Double> scores = rankingService.calculateScores(
                filteredListings,
                request.getTargetBudget(),
                request.getKeyword(),
                request.getMinBedrooms()
            );

            List<ListingDTO> rankedListings = new ArrayList<>();
            for (Listing listing : filteredListings) {
                ListingDTO dto = convertToDTO(listing, scores.getOrDefault(listing.getId(), 0.0));
                rankedListings.add(dto);
            }
            rankedListings.sort(Comparator.comparingDouble(ListingDTO::getRelevanceScore).reversed());

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
            e.printStackTrace();
            return buildErrorResponse(e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return buildErrorResponse("Null value encountered: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse("An unexpected error occurred: " + e.getClass().getSimpleName());
        }
    }

    public ListingDTO getListingById(String id) {
        return listingStorage.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    private List<Listing> filterListings(List<Listing> listings, SearchRequest request) {
        return listings.stream()
                .filter(l -> {
                    if (request.getCity() != null && !l.getCity().equalsIgnoreCase(request.getCity())) return false;
                    if (request.getMinPrice() != null && l.getPrice().compareTo(request.getMinPrice()) < 0) return false;
                    if (request.getMaxPrice() != null && l.getPrice().compareTo(request.getMaxPrice()) > 0) return false;
                    if (request.getMinBedrooms() != null && l.getBedrooms() < request.getMinBedrooms()) return false;
                    if (request.getKeyword() != null) {
                        String keyword = request.getKeyword().toLowerCase();
                        boolean descMatch = l.getDescription() != null && l.getDescription().toLowerCase().contains(keyword);
                        boolean addrMatch = l.getAddress() != null && l.getAddress().toLowerCase().contains(keyword);
                        if (!descMatch && !addrMatch) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private ListingDTO convertToDTO(Listing listing) {
        return convertToDTO(listing, null);
    }

    private ListingDTO convertToDTO(Listing listing, Double relevanceScore) {
        return new ListingDTO(
                listing.getId(),
                listing.getAddress(),
                listing.getCity(),
                listing.getState(),
                listing.getZip(),
                listing.getPrice(),
                listing.getBedrooms(),
                listing.getBathrooms(),
                listing.getSqft(),
                listing.getLatitude(),
                listing.getLongitude(),
                listing.getListedDate(),
                listing.getStatus(),
                listing.getDescription(),
                relevanceScore
        );
    }

    private SearchResponse buildErrorResponse(String message) {
        SearchResponse response = new SearchResponse();
        response.setSuccess(false);
        response.setErrorMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public void seedTestData() {
        if (listingStorage.count() > 0) {
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

        listingStorage.saveAll(testListings);
    }
}

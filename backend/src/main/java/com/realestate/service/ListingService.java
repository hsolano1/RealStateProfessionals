package com.realestate.service;

import com.realestate.dto.ListingDTO;
import com.realestate.dto.SearchRequest;
import com.realestate.dto.SearchResponse;
import com.realestate.entity.Listing;
import com.realestate.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingService {

    private final ListingRepository listingRepository;
    private final RankingService rankingService;

    public SearchResponse search(SearchRequest request) {
        try {
            request.validate();

            List<Listing> allMatches = listingRepository.searchListingsNoPage(
                request.city,
                request.minPrice,
                request.maxPrice,
                request.minBedrooms,
                request.keyword
            );

            Map<String, Double> scores = rankingService.calculateScores(
                allMatches,
                request.targetBudget,
                request.keyword,
                request.minBedrooms
            );

            List<ListingDTO> rankedListings = allMatches.stream()
                    .map(listing -> convertToDTO(listing, scores.getOrDefault(listing.getId(), 0.0)))
                    .sorted(Comparator.comparingDouble(ListingDTO::getRelevanceScore).reversed())
                    .collect(Collectors.toList());

            int totalCount = rankedListings.size();
            int pageSize = request.pageSize;
            int pageNumber = request.pageNumber;
            int totalPages = (totalCount + pageSize - 1) / pageSize;

            if (pageNumber > totalPages && totalCount > 0) {
                return buildErrorResponse("Page number exceeds available pages");
            }

            List<ListingDTO> pageResults = rankedListings.stream()
                    .skip((long) (pageNumber - 1) * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            return SearchResponse.builder()
                    .success(true)
                    .data(SearchResponse.SearchData.builder()
                            .listings(pageResults)
                            .totalCount((long) totalCount)
                            .pageNumber(pageNumber)
                            .pageSize(pageSize)
                            .totalPages(totalPages)
                            .build())
                    .timestamp(LocalDateTime.now())
                    .build();

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
        return SearchResponse.builder()
                .success(false)
                .errorMessage(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public void seedTestData() {
        if (listingRepository.count() > 0) {
            return;
        }

        List<Listing> testListings = Arrays.asList(
            Listing.builder()
                .sourceId("A1")
                .sourceSystem("MLS_A")
                .address("123 Main St, Apt 4B")
                .city("Springfield")
                .state("VA")
                .zip("22150")
                .price(new BigDecimal("450000"))
                .bedrooms(2)
                .bathrooms(1.5)
                .sqft(980)
                .latitude(38.7893)
                .longitude(-77.1873)
                .listedDate(java.time.LocalDate.of(2026, 8, 29))
                .status("active")
                .description("Bright top-floor condo near shops and transit. Pet friendly.")
                .build(),
            Listing.builder()
                .sourceId("A2")
                .sourceSystem("MLS_A")
                .address("456 Oak Ave")
                .city("Springfield")
                .state("VA")
                .zip("22150")
                .price(new BigDecimal("525000"))
                .bedrooms(3)
                .bathrooms(2.0)
                .sqft(1450)
                .latitude(38.7791)
                .longitude(-77.1901)
                .listedDate(java.time.LocalDate.of(2026, 9, 2))
                .status("active")
                .description("Updated kitchen, fenced yard, close to schools.")
                .build(),
            Listing.builder()
                .sourceId("A3")
                .sourceSystem("MLS_A")
                .address("789 Pine Rd")
                .city("Fairfax")
                .state("VA")
                .zip("22030")
                .price(new BigDecimal("399000"))
                .bedrooms(2)
                .bathrooms(1.0)
                .sqft(850)
                .latitude(38.8462)
                .longitude(-77.3064)
                .listedDate(java.time.LocalDate.of(2026, 9, 1))
                .status("active")
                .description("Cozy starter home, no pets.")
                .build(),
            Listing.builder()
                .sourceId("A4")
                .sourceSystem("MLS_A")
                .address("22 Birch Ln")
                .city("Reston")
                .state("VA")
                .zip("20190")
                .price(new BigDecimal("610000"))
                .bedrooms(4)
                .bathrooms(3.0)
                .sqft(2100)
                .latitude(38.9586)
                .longitude(-77.3570)
                .listedDate(java.time.LocalDate.of(2026, 9, 3))
                .status("active")
                .description("Spacious family home near Reston Town Center. Pets welcome.")
                .build(),
            Listing.builder()
                .sourceId("A5")
                .sourceSystem("MLS_A")
                .address("55 Elm Ct")
                .city("Vienna")
                .state("VA")
                .zip("22180")
                .price(new BigDecimal("470000"))
                .bedrooms(3)
                .bathrooms(2.0)
                .sqft(1300)
                .latitude(38.9012)
                .longitude(-77.2653)
                .listedDate(java.time.LocalDate.of(2026, 9, 4))
                .status("active")
                .description("Quiet cul-de-sac, walkable to Metro. No pets.")
                .build(),
            Listing.builder()
                .sourceId("A6")
                .sourceSystem("MLS_A")
                .address("300 Cedar Blvd")
                .city("Manassas")
                .state("VA")
                .zip("20110")
                .price(new BigDecimal("415000"))
                .bedrooms(3)
                .bathrooms(2.0)
                .sqft(1600)
                .latitude(38.7509)
                .longitude(-77.4753)
                .listedDate(java.time.LocalDate.of(2026, 8, 10))
                .status("active")
                .description("Split-level home, large driveway, pets allowed.")
                .build(),
            Listing.builder()
                .sourceId("A7")
                .sourceSystem("MLS_A")
                .address("42 Willow Way")
                .city("Chantilly")
                .state("VA")
                .zip("20151")
                .price(new BigDecimal("540000"))
                .bedrooms(4)
                .bathrooms(2.5)
                .sqft(1950)
                .latitude(38.8909)
                .longitude(-77.4316)
                .listedDate(java.time.LocalDate.of(2026, 7, 28))
                .status("pending")
                .description("Corner lot, recently painted, no pets due to HOA.")
                .build(),
            Listing.builder()
                .sourceId("B7")
                .sourceSystem("MLS_B")
                .address("123 Main Street, Unit 4B")
                .city("Springfield")
                .state("VA")
                .zip("22150")
                .price(new BigDecimal("452000"))
                .bedrooms(2)
                .bathrooms(1.5)
                .sqft(980)
                .latitude(38.7893)
                .longitude(-77.1873)
                .listedDate(java.time.LocalDate.of(2026, 8, 27))
                .status("active")
                .description("Top floor condo, walk to shopping. Pets allowed.")
                .build(),
            Listing.builder()
                .sourceId("B8")
                .sourceSystem("MLS_B")
                .address("456 Oak Avenue")
                .city("Springfield")
                .state("VA")
                .zip("22151")
                .price(new BigDecimal("527500"))
                .bedrooms(3)
                .bathrooms(2.0)
                .sqft(1450)
                .latitude(38.7791)
                .longitude(-77.1901)
                .listedDate(java.time.LocalDate.of(2026, 8, 30))
                .status("active")
                .description("Renovated kitchen, fenced backyard, near schools.")
                .build(),
            Listing.builder()
                .sourceId("B10")
                .sourceSystem("MLS_B")
                .address("100 Maple Dr")
                .city("Reston")
                .state("VA")
                .zip("20190")
                .price(new BigDecimal("585000"))
                .bedrooms(3)
                .bathrooms(2.5)
                .sqft(1900)
                .latitude(38.9601)
                .longitude(-77.3499)
                .listedDate(java.time.LocalDate.of(2026, 8, 20))
                .status("active")
                .description("Townhome with 2-car garage, community pool.")
                .build()
        );

        listingRepository.saveAll(testListings);
        log.info("Test data seeded: {} listings", testListings.size());
    }
}

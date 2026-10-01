package com.realestate.service;

import com.realestate.entity.Listing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RankingServiceTest {

    private RankingService rankingService;

    @BeforeEach
    void setUp() {
        rankingService = new RankingService();
    }

    @Test
    void testCalculateScoresWithEmptyList() {
        Map<String, Double> scores = rankingService.calculateScores(
                Arrays.asList(),
                new BigDecimal("450000"),
                "pet friendly",
                2
        );

        assertTrue(scores.isEmpty());
    }

    @Test
    void testCalculateScoresWithPerfectMatch() {
        Listing listing = Listing.builder()
                .id("1")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now())
                .bedrooms(2)
                .description("Pet friendly home")
                .build();

        Map<String, Double> scores = rankingService.calculateScores(
                Arrays.asList(listing),
                new BigDecimal("450000"),
                "pet friendly",
                2
        );

        assertNotNull(scores.get("1"));
        assertTrue(scores.get("1") > 70);
    }

    @Test
    void testCalculateScoresWithPriceDeviation() {
        Listing listing1 = Listing.builder()
                .id("1")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now())
                .bedrooms(2)
                .description("Home")
                .build();

        Listing listing2 = Listing.builder()
                .id("2")
                .price(new BigDecimal("500000"))
                .listedDate(LocalDate.now())
                .bedrooms(2)
                .description("Home")
                .build();

        Map<String, Double> scores = rankingService.calculateScores(
                Arrays.asList(listing1, listing2),
                new BigDecimal("450000"),
                null,
                2
        );

        assertTrue(scores.get("1") > scores.get("2"));
    }

    @Test
    void testCalculateScoresWithRecency() {
        Listing recent = Listing.builder()
                .id("1")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now())
                .bedrooms(2)
                .description("Home")
                .build();

        Listing old = Listing.builder()
                .id("2")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now().minusDays(100))
                .bedrooms(2)
                .description("Home")
                .build();

        Map<String, Double> scores = rankingService.calculateScores(
                Arrays.asList(recent, old),
                new BigDecimal("450000"),
                null,
                2
        );

        assertTrue(scores.get("1") > scores.get("2"));
    }

    @Test
    void testCalculateScoresWithBedroomMatch() {
        Listing exact = Listing.builder()
                .id("1")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now())
                .bedrooms(2)
                .description("Home")
                .build();

        Listing excess = Listing.builder()
                .id("2")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now())
                .bedrooms(4)
                .description("Home")
                .build();

        Listing insufficient = Listing.builder()
                .id("3")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now())
                .bedrooms(1)
                .description("Home")
                .build();

        Map<String, Double> scores = rankingService.calculateScores(
                Arrays.asList(exact, excess, insufficient),
                new BigDecimal("450000"),
                null,
                2
        );

        assertTrue(scores.get("1") > scores.get("2"));
        assertTrue(scores.get("1") > scores.get("3"));
    }

    @Test
    void testCalculateScoresWithKeywordMatch() {
        Listing fullMatch = Listing.builder()
                .id("1")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now())
                .bedrooms(2)
                .description("Pet friendly home with parking")
                .build();

        Listing partialMatch = Listing.builder()
                .id("2")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now())
                .bedrooms(2)
                .description("Great home with garden")
                .build();

        Listing noMatch = Listing.builder()
                .id("3")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now())
                .bedrooms(2)
                .description("Nice house")
                .build();

        Map<String, Double> scores = rankingService.calculateScores(
                Arrays.asList(fullMatch, partialMatch, noMatch),
                new BigDecimal("450000"),
                "pet friendly",
                2
        );

        assertTrue(scores.get("1") > scores.get("2"));
        assertTrue(scores.get("2") > scores.get("3"));
    }

    @Test
    void testCalculateScoresScaleIsBetweenZeroAndHundred() {
        Listing listing = Listing.builder()
                .id("1")
                .price(new BigDecimal("450000"))
                .listedDate(LocalDate.now())
                .bedrooms(2)
                .description("Home")
                .build();

        Map<String, Double> scores = rankingService.calculateScores(
                Arrays.asList(listing),
                new BigDecimal("450000"),
                null,
                2
        );

        Double score = scores.get("1");
        assertNotNull(score);
        assertTrue(score >= 0 && score <= 100);
    }

    @Test
    void testCalculateScoresWithNullListings() {
        assertThrows(NullPointerException.class, () ->
            rankingService.calculateScores(
                    null,
                    new BigDecimal("450000"),
                    null,
                    2
            )
        );
    }
}

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
        Listing listing = new Listing("src1", "MLS", "123 Main St", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 2, 1.5, 1000, 38.78, -77.18, LocalDate.now(), "active", "Pet friendly home");
        listing.setId("1");

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
        Listing listing1 = new Listing("src1", "MLS", "123 Main St", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 2, 1.5, 1000, 38.78, -77.18, LocalDate.now(), "active", "Home");
        listing1.setId("1");

        Listing listing2 = new Listing("src2", "MLS", "456 Oak Ave", "Springfield", "VA", "22150",
                new BigDecimal("500000"), 2, 1.5, 1000, 38.78, -77.18, LocalDate.now(), "active", "Home");
        listing2.setId("2");

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
        Listing recent = new Listing("src1", "MLS", "123 Main St", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 2, 1.5, 1000, 38.78, -77.18, LocalDate.now(), "active", "Home");
        recent.setId("1");

        Listing old = new Listing("src2", "MLS", "456 Oak Ave", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 2, 1.5, 1000, 38.78, -77.18, LocalDate.now().minusDays(100), "active", "Home");
        old.setId("2");

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
        Listing exact = new Listing("src1", "MLS", "123 Main St", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 2, 1.5, 1000, 38.78, -77.18, LocalDate.now(), "active", "Home");
        exact.setId("1");

        Listing excess = new Listing("src2", "MLS", "456 Oak Ave", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 4, 2.5, 1500, 38.78, -77.18, LocalDate.now(), "active", "Home");
        excess.setId("2");

        Listing insufficient = new Listing("src3", "MLS", "789 Pine Rd", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 1, 1.0, 800, 38.78, -77.18, LocalDate.now(), "active", "Home");
        insufficient.setId("3");

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
        Listing fullMatch = new Listing("src1", "MLS", "123 Main St", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 2, 1.5, 1000, 38.78, -77.18, LocalDate.now(), "active", "Pet friendly home with parking");
        fullMatch.setId("1");

        Listing partialMatch = new Listing("src2", "MLS", "456 Oak Ave", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 2, 1.5, 1000, 38.78, -77.18, LocalDate.now(), "active", "Great home with garden");
        partialMatch.setId("2");

        Listing noMatch = new Listing("src3", "MLS", "789 Pine Rd", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 2, 1.5, 1000, 38.78, -77.18, LocalDate.now(), "active", "Nice house");
        noMatch.setId("3");

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
        Listing listing = new Listing("src1", "MLS", "123 Main St", "Springfield", "VA", "22150",
                new BigDecimal("450000"), 2, 1.5, 1000, 38.78, -77.18, LocalDate.now(), "active", "Home");
        listing.setId("1");

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

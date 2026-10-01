package com.realestate.service;

import com.realestate.entity.Listing;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class RankingService {

    private static final int MAX_DAYS_SINCE_LISTING = 365;
    private static final double PRICE_WEIGHT = 0.40;
    private static final double RECENCY_WEIGHT = 0.30;
    private static final double BEDROOM_WEIGHT = 0.20;
    private static final double KEYWORD_WEIGHT = 0.10;

    public Map<String, Double> calculateScores(List<Listing> listings, BigDecimal targetBudget, String keyword, Integer minBedrooms) {
        Map<String, Double> scores = new HashMap<>();

        if (listings.isEmpty()) {
            return scores;
        }

        for (Listing listing : listings) {
            double priceScore = calculatePriceScore(listing.getPrice(), targetBudget);
            double recencyScore = calculateRecencyScore(listing.getListedDate());
            double bedroomScore = calculateBedroomScore(listing.getBedrooms(), minBedrooms);
            double keywordScore = calculateKeywordScore(listing.getDescription(), keyword);

            double relevanceScore = (PRICE_WEIGHT * priceScore)
                    + (RECENCY_WEIGHT * recencyScore)
                    + (BEDROOM_WEIGHT * bedroomScore)
                    + (KEYWORD_WEIGHT * keywordScore);

            scores.put(listing.getId(), relevanceScore * 100);
        }

        return scores;
    }

    private double calculatePriceScore(BigDecimal listingPrice, BigDecimal targetBudget) {
        if (listingPrice == null || targetBudget == null || targetBudget.signum() == 0) {
            return 0.0;
        }

        BigDecimal difference = listingPrice.subtract(targetBudget).abs();
        BigDecimal ratio = difference.divide(targetBudget, 4, java.math.RoundingMode.HALF_UP);

        return Math.max(0, 1.0 - ratio.doubleValue());
    }

    private double calculateRecencyScore(LocalDate listedDate) {
        if (listedDate == null) {
            return 0.0;
        }

        long daysSinceListing = ChronoUnit.DAYS.between(listedDate, LocalDate.now());
        if (daysSinceListing < 0) {
            return 0.0;
        }

        return Math.max(0, 1.0 - (double) daysSinceListing / MAX_DAYS_SINCE_LISTING);
    }

    private double calculateBedroomScore(Integer listingBedrooms, Integer targetBedrooms) {
        if (listingBedrooms == null) {
            return 0.0;
        }

        if (targetBedrooms == null) {
            return 0.5;
        }

        if (listingBedrooms.equals(targetBedrooms)) {
            return 1.0;
        }

        if (listingBedrooms > targetBedrooms) {
            return Math.max(0, 1.0 - (listingBedrooms - targetBedrooms) * 0.1);
        }

        return Math.max(0, 1.0 - (targetBedrooms - listingBedrooms) * 0.15);
    }

    private double calculateKeywordScore(String description, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return 0.5;
        }

        if (description == null || description.trim().isEmpty()) {
            return 0.0;
        }

        String lowerDescription = description.toLowerCase();
        String lowerKeyword = keyword.toLowerCase().trim();

        if (lowerDescription.contains(lowerKeyword)) {
            return 1.0;
        }

        String[] keywords = lowerKeyword.split("\\s+");
        int matchedWords = 0;
        for (String word : keywords) {
            if (lowerDescription.contains(word)) {
                matchedWords++;
            }
        }

        if (keywords.length > 0) {
            return Math.min(1.0, (double) matchedWords / keywords.length * 0.7);
        }

        return 0.0;
    }
}

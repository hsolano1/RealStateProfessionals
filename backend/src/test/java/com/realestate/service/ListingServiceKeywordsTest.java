package com.realestate.service;

import com.realestate.entity.Listing;
import com.realestate.storage.ListingStorage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ListingServiceKeywordsTest {

    @Test
    void returnsOnlyKeywordsPresentInDescriptions() {
        ListingStorage storage = Mockito.mock(ListingStorage.class);
        Mockito.when(storage.findAll()).thenReturn(List.of(
                listing("Bright condo near shops and transit. Pet friendly."),
                listing("Townhome with 2-car garage, community pool.")
        ));
        ListingService service = new ListingService(storage, new RankingService());

        List<String> keywords = service.getAvailableKeywords();

        assertTrue(keywords.contains("pet friendly"));
        assertTrue(keywords.contains("transit"));
        assertTrue(keywords.contains("garage"));
        assertTrue(keywords.contains("pool"));
        assertFalse(keywords.contains("waterfront"));
    }

    @Test
    void returnsEmptyListWhenNoListings() {
        ListingStorage storage = Mockito.mock(ListingStorage.class);
        Mockito.when(storage.findAll()).thenReturn(List.of());
        ListingService service = new ListingService(storage, new RankingService());

        assertEquals(List.of(), service.getAvailableKeywords());
    }

    @Test
    void ignoresListingsWithoutDescription() {
        ListingStorage storage = Mockito.mock(ListingStorage.class);
        Mockito.when(storage.findAll()).thenReturn(List.of(listing(null)));
        ListingService service = new ListingService(storage, new RankingService());

        assertEquals(List.of(), service.getAvailableKeywords());
    }

    private Listing listing(String description) {
        return new Listing("S1", "TEST", "1 Test St", "Springfield", "VA", "22150",
                new BigDecimal("400000"), 2, 1.0, 900, null, null,
                LocalDate.of(2026, 9, 1), "active", description);
    }
}

package com.realestate.config;

import com.realestate.service.ListingService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final ListingService listingService;

    public DataInitializer(ListingService listingService) {
        this.listingService = listingService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        listingService.seedTestData();
    }
}

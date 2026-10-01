package com.realestate.config;

import com.realestate.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ListingService listingService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        listingService.seedTestData();
    }
}

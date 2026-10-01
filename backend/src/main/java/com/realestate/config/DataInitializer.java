package com.realestate.config;

import com.realestate.service.ListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final ListingService listingService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        log.info("Initializing test data...");
        listingService.seedTestData();
    }
}

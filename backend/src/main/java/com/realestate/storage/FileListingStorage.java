package com.realestate.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.entity.Listing;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "file", matchIfMissing = false)
public class FileListingStorage implements ListingStorage {
    private static final String DATA_FILE = "data/listings.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Listing> listings = new ConcurrentHashMap<>();

    public FileListingStorage() {
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            Path path = Paths.get(DATA_FILE);
            if (Files.exists(path)) {
                String json = Files.readString(path);
                ListingData data = objectMapper.readValue(json, ListingData.class);
                for (Listing listing : data.listings) {
                    listings.put(listing.getId(), listing);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading listings from file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try {
            Path dirPath = Paths.get("data");
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve("listings.json");
            ListingData data = new ListingData();
            data.listings = new ArrayList<>(listings.values());
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            Files.writeString(filePath, json);
        } catch (IOException e) {
            System.err.println("Error saving listings to file: " + e.getMessage());
        }
    }

    @Override
    public List<Listing> findAll() {
        return new ArrayList<>(listings.values());
    }

    @Override
    public Optional<Listing> findById(String id) {
        return Optional.ofNullable(listings.get(id));
    }

    @Override
    public Listing save(Listing listing) {
        if (listing.getId() == null) {
            listing.setId(UUID.randomUUID().toString());
        }
        listings.put(listing.getId(), listing);
        saveToFile();
        return listing;
    }

    @Override
    public List<Listing> saveAll(List<Listing> listingsToSave) {
        for (Listing listing : listingsToSave) {
            if (listing.getId() == null) {
                listing.setId(UUID.randomUUID().toString());
            }
            listings.put(listing.getId(), listing);
        }
        saveToFile();
        return listingsToSave;
    }

    @Override
    public long count() {
        return listings.size();
    }

    @Override
    public void deleteAll() {
        listings.clear();
        saveToFile();
    }

    private static class ListingData {
        public List<Listing> listings = new ArrayList<>();
    }
}

package com.realestate.storage;

import com.realestate.entity.Listing;
import com.realestate.repository.ListingRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "postgres", matchIfMissing = true)
public class PostgresListingStorage implements ListingStorage {

    private final ListingRepository listingRepository;

    public PostgresListingStorage(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    public List<Listing> findAll() {
        return listingRepository.findAll();
    }

    @Override
    public Optional<Listing> findById(String id) {
        return listingRepository.findById(id);
    }

    @Override
    public Listing save(Listing listing) {
        return listingRepository.save(listing);
    }

    @Override
    public List<Listing> saveAll(List<Listing> listings) {
        return listingRepository.saveAll(listings);
    }

    @Override
    public long count() {
        return listingRepository.count();
    }

    @Override
    public void deleteAll() {
        listingRepository.deleteAll();
    }
}

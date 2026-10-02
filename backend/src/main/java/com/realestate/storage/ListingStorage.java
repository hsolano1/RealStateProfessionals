package com.realestate.storage;

import com.realestate.entity.Listing;
import java.util.List;
import java.util.Optional;

public interface ListingStorage {
    List<Listing> findAll();
    Optional<Listing> findById(String id);
    Listing save(Listing listing);
    List<Listing> saveAll(List<Listing> listings);
    long count();
    void deleteAll();
}

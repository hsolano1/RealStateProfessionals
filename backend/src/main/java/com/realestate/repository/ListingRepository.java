package com.realestate.repository;

import com.realestate.entity.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, String> {

    List<Listing> findByCity(String city);

    List<Listing> findByStatus(String status);

    List<Listing> findByBedroomsGreaterThanEqual(Integer bedrooms);

    Page<Listing> findByCity(String city, Pageable pageable);

    @Query("""
        SELECT l FROM Listing l WHERE
        (:city IS NULL OR LOWER(l.city) = LOWER(:city))
        AND (:minPrice IS NULL OR l.price >= :minPrice)
        AND (:maxPrice IS NULL OR l.price <= :maxPrice)
        AND (:minBedrooms IS NULL OR l.bedrooms >= :minBedrooms)
        AND (:keyword IS NULL OR
             LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(l.address) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY l.listedDate DESC
        """)
    Page<Listing> searchListings(
        @Param("city") String city,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minBedrooms") Integer minBedrooms,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    @Query("""
        SELECT l FROM Listing l WHERE
        (:city IS NULL OR LOWER(l.city) = LOWER(:city))
        AND (:minPrice IS NULL OR l.price >= :minPrice)
        AND (:maxPrice IS NULL OR l.price <= :maxPrice)
        AND (:minBedrooms IS NULL OR l.bedrooms >= :minBedrooms)
        AND (:keyword IS NULL OR
             LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(l.address) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    List<Listing> searchListingsNoPage(
        @Param("city") String city,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minBedrooms") Integer minBedrooms,
        @Param("keyword") String keyword
    );

    boolean existsBySourceIdAndSourceSystem(String sourceId, String sourceSystem);
}

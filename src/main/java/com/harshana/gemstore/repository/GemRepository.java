package com.harshana.gemstore.repository;

import com.harshana.gemstore.entity.Gem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GemRepository extends JpaRepository<Gem, Long> {

    boolean existsByCategoryId(Long categoryId);

    @Query("""
        SELECT g FROM Gem g
        WHERE (:categoryId IS NULL OR g.category.id = :categoryId)
          AND (:minPrice IS NULL OR g.price >= :minPrice)
          AND (:maxPrice IS NULL OR g.price <= :maxPrice)
          AND (:minCarat IS NULL OR g.carat >= :minCarat)
          AND (:maxCarat IS NULL OR g.carat <= :maxCarat)
          AND (g.status = 'AVAILABLE')
        """)
    List<Gem> filterGems(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minCarat") Double minCarat,
            @Param("maxCarat") Double maxCarat
    );
}

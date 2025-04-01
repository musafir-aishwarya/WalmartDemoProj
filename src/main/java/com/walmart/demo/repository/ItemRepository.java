package com.walmart.demo.repository;

import com.walmart.demo.model.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	
	Page<Item> findAll(Pageable pageable);
	
	List<Item> findByNameContainingIgnoreCase(String name);

    List<Item> findByDescriptionContainingIgnoreCase(String description);

    @Query("SELECT i FROM Item i WHERE i.price BETWEEN :minPrice AND :maxPrice")
    List<Item> findByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    Page<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);

	List<Item> findByImageUrlIn(List<String> similarImageUrls);

}

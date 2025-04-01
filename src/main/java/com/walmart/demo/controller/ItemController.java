package com.walmart.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.walmart.demo.DTO.ItemDTO;
import com.walmart.demo.model.Item;
import com.walmart.demo.service.ItemService;

@CrossOrigin(origins = "http://localhost:3000") 
@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;
	/*
	 * @PostMapping(value = "/save", consumes =
	 * {MediaType.MULTIPART_FORM_DATA_VALUE}) public ResponseEntity<Item> saveItem(
	 * 
	 * @RequestParam("name") String name,
	 * 
	 * @RequestParam("description") String description,
	 * 
	 * @RequestParam("price") double price,
	 * 
	 * @RequestParam("image") MultipartFile image) {
	 * 
	 * Item savedItem = itemService.saveItem(name, description, price, image);
	 * return ResponseEntity.ok(savedItem); }
	 */
    
 // In your controller
    @PostMapping("/save")
    public ResponseEntity<ItemDTO> saveItem(
        @RequestParam String name,
        @RequestParam String description,
        @RequestParam double price,
        @RequestParam MultipartFile image) throws IOException {
        
        // Save the item and get the full image URL
        Item savedItem = itemService.saveItem(name, description, price, image);
        
        // Convert to DTO with full image URL
        ItemDTO dto = new ItemDTO();
        dto.setName(savedItem.getName());
        dto.setDescription(savedItem.getDescription());
        dto.setPrice(savedItem.getPrice());
        dto.setImagePath("http://localhost:8080/images/" + savedItem.getImageUrl());
        
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/saveAll")
    public ResponseEntity<List<Item>> saveAllItems(@RequestBody List<Item> items) {
        return ResponseEntity.ok(itemService.saveAllItems(items));
    }

    @GetMapping
    public ResponseEntity<Page<ItemDTO>> getAllItems(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(itemService.getAllItems(pageable));
    }

    // Search items by name, description, or price
    @GetMapping("/search")
    public ResponseEntity<Page<ItemDTO>> searchItems(@RequestParam String query, Pageable pageable) {
        return ResponseEntity.ok(itemService.searchItems(query, pageable));
    }
}
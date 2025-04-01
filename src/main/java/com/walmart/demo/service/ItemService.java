package com.walmart.demo.service;

import com.walmart.demo.DTO.ItemDTO;
import com.walmart.demo.model.Item;
import com.walmart.demo.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    @Value("${image.base-url}") // Configure in application.properties
    private String baseUrl;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private ItemRepository itemRepository;

    // Single unified DTO conversion method
    private ItemDTO convertToDto(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            // Remove any double slashes
            String cleanBaseUrl = baseUrl.replaceAll("(?<!http:)//", "/");
            dto.setImagePath(cleanBaseUrl + "images/" + item.getImageUrl());
        }
        
        return dto;
    }

    public Item saveItem(String name, String description, double price, MultipartFile image) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String filename = UUID.randomUUID().toString() + 
                        "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Create and save item
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setImageUrl(filename);
        
        return itemRepository.save(item);
    }

    @Transactional
    public List<Item> saveAllItems(List<Item> items) {
        return itemRepository.saveAll(items);
    }

    public Page<ItemDTO> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable).map(this::convertToDto);
    }

    public Page<ItemDTO> searchItems(String query, Pageable pageable) {
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            query, query, pageable).map(this::convertToDto);
    }

    public List<Item> searchByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Item> searchByDescription(String description) {
        return itemRepository.findByDescriptionContainingIgnoreCase(description);
    }

    public List<Item> searchByPriceRange(double minPrice, double maxPrice) {
        return itemRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    
}
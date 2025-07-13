package com.hari.service;

import org.springframework.stereotype.Service;

import com.hari.dto.UrlRequest;
import com.hari.dto.UrlResponse;
import com.hari.exception.UrlExpiredException;
import com.hari.exception.UrlNotFoundException;
import com.hari.model.UrlMapping;
import com.hari.repository.UrlMappingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UrlMappingService {

    private final UrlMappingRepository repository;
    private final String BASE_URL = "http://localhost:8080/";

    public UrlMappingService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    public UrlResponse createShortUrl(UrlRequest request) {
        String originalUrl = request.getOriginalUrl().trim();

        // ✅ 1. Validate URL format
        if (!originalUrl.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")) {
            throw new IllegalArgumentException("❌ Invalid URL format");
        }

        // ✅ 2. Check for existing entry
        Optional<UrlMapping> existingMappingOpt = repository.findByOriginalUrl(originalUrl);

        if (existingMappingOpt.isPresent()) {
            UrlMapping existingMapping = existingMappingOpt.get();

            if (existingMapping.getExpiresAt().isAfter(LocalDateTime.now())) {
                // 🔒 Already exists and is still valid → Reject as duplicate
                throw new IllegalArgumentException("❌ This URL has already been shortened and is still valid.");
            } else {
                // 🔄 Exists but expired → delete and allow regeneration
                repository.delete(existingMapping);
            }
        }

        // ✅ 3. Create new short URL
        String shortId = UUID.randomUUID().toString().substring(0, 6);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);

        UrlMapping newMapping = new UrlMapping(null, originalUrl, shortId, now, expiresAt);
        repository.save(newMapping);

        return new UrlResponse(BASE_URL + shortId, expiresAt);
    }


    public String getOriginalUrl(String shortId) {
        UrlMapping mapping = repository.findByShortUrl(shortId)
                .orElseThrow(() -> new UrlNotFoundException("❌ Short URL not found"));

        if (mapping.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("⏰ Short URL has expired");
        }

        return mapping.getOriginalUrl();
    }
    
 // READ ALL
    public List<UrlResponse> getAllShortUrls() {
        return repository.findAll().stream()
                .map(mapping -> new UrlResponse(
                        BASE_URL + mapping.getShortUrl(),
                        mapping.getOriginalUrl(),
                        mapping.getCreatedAt(),
                        mapping.getExpiresAt()
                ))
                .collect(Collectors.toList());
    }

    // UPDATE
    public UrlResponse updateOriginalUrl(String shortId, String newOriginalUrl) {
        UrlMapping mapping = repository.findByShortUrl(shortId)
                .orElseThrow(() -> new UrlNotFoundException("❌ Short URL not found"));
        System.out.println(shortId);
        System.out.println(newOriginalUrl);
        if (!newOriginalUrl.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")) {
            throw new IllegalArgumentException("❌ Invalid new URL format.");
        }

        mapping.setOriginalUrl(newOriginalUrl);
        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        repository.save(mapping);

        return new UrlResponse(
                BASE_URL + mapping.getShortUrl(),
                mapping.getOriginalUrl(),
                mapping.getCreatedAt(),
                mapping.getExpiresAt()
        );
    }

    // DELETE
    public void deleteShortUrl(String shortId) {
        UrlMapping mapping = repository.findByShortUrl(shortId)
                .orElseThrow(() -> new UrlNotFoundException("❌ Short URL not found"));
        repository.delete(mapping);
    }

}
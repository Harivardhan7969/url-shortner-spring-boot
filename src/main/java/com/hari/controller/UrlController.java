package com.hari.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hari.dto.UrlRequest;
import com.hari.dto.UrlResponse;
import com.hari.service.UrlMappingService;

import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/")
@CrossOrigin(originPatterns = "http://localhost:5173/")
public class UrlController {

    private final UrlMappingService service;

    public UrlController(UrlMappingService service) {
        this.service = service;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shortenUrl(@RequestBody UrlRequest request) {
        UrlResponse response = service.createShortUrl(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortId}")
    public ResponseEntity<Void> redirect(@PathVariable(name = "shortId") String shortId) {
        String originalUrl = service.getOriginalUrl(shortId);
        HttpHeaders headers = new HttpHeaders();
        System.out.println("original url");
        System.out.println(originalUrl);
        headers.add("Location", originalUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
    
    @GetMapping("/urls")
    public ResponseEntity<List<UrlResponse>> getAllUrls() {
        return ResponseEntity.ok(service.getAllShortUrls());
    }

    @PutMapping("/{shortId}")
    public ResponseEntity<UrlResponse> updateUrl(@PathVariable(name = "shortId") String shortId, @RequestBody  UrlRequest request) {
       
    	System.out.println(request.getOriginalUrl());
    	UrlResponse updated = service.updateOriginalUrl(shortId, request.getOriginalUrl());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{shortId}")
    public ResponseEntity<String> deleteUrl(@PathVariable(name = "shortId") String shortId) {
        
    	service.deleteShortUrl(shortId);
        return ResponseEntity.ok("✅ Short URL deleted successfully.");
    }

}
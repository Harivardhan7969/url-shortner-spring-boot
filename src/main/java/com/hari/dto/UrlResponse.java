package com.hari.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//@Data
//@AllArgsConstructor
//public class UrlResponse {
//    private String shortUrl;
//    private LocalDateTime expiresAt;
//}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlResponse {
   
	private String shortUrl;
    
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    public UrlResponse(String shortUrl, LocalDateTime expiresAt) {
		super();
		this.shortUrl = shortUrl;
		this.expiresAt = expiresAt;
	}
   
}

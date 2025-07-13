package com.hari.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "url_mappings")
public class UrlMapping {

    @Id
    private String id;

    @Indexed(unique = true)
    private String originalUrl;

    private String shortUrl;
    private LocalDateTime createdAt;

    @Indexed(name = "expiry_idx", expireAfterSeconds = 0)
    private LocalDateTime expiresAt;
}

package com.example.backnut.controllers;

import com.example.backnut.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController                    // ↔ @Controller + @ResponseBody sur chaque méthode
@RequestMapping("/api/chat")
public class FileStorageController {

    @Autowired
    private FileStorageService storageService;

    /**
     * Upload d’image.
     * ↪️ Consomme explicitement du multipart/form-data.
     */
    @PostMapping(
            path = "/upload/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String,String>> uploadImage(
            @RequestPart("file") MultipartFile file   // @RequestPart pour multipart
    ) {
        String filename = storageService.storeFile(file);
        String url = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/chat/images/")
                .path(filename)
                .toUriString();
        return ResponseEntity
                .ok(Map.of("url", url));
    }

    /**
     * Upload d’audio.
     */
    @PostMapping(
            path = "/upload/audio",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String,String>> uploadAudio(
            @RequestPart("file") MultipartFile file
    ) {
        String filename = storageService.storeFile(file);
        String url = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/chat/audio/")
                .path(filename)
                .toUriString();
        return ResponseEntity
                .ok(Map.of("url", url));
    }

    /**
     * Servir l’image en inline.
     */
    @GetMapping(
            path = "/images/{filename:.+}",
            produces = MediaType.IMAGE_JPEG_VALUE   // ou application/octet-stream selon le type
    )
    public ResponseEntity<Resource> serveImage(
            @PathVariable String filename
    ) throws MalformedURLException {
        Path path = storageService.loadFile(filename);
        Resource resource = new org.springframework.core.io.UrlResource(path.toUri());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Servir l’audio en téléchargement.
     */
    @GetMapping(
            path = "/audio/{filename:.+}"
    )
    public ResponseEntity<ResourceRegion> serveAudio(
            @RequestHeader HttpHeaders requestHeaders,
            @PathVariable String filename
    ) throws IOException {
        // 1️⃣ Charge la ressource
        Path path = storageService.loadFile(filename);
        UrlResource resource = new UrlResource(path.toUri());
        long contentLength = resource.contentLength();

        // 2️⃣ Détermine la plage demandée
        List<HttpRange> ranges = requestHeaders.getRange();
        ResourceRegion region;
        if (!ranges.isEmpty()) {
            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(contentLength);
            long end   = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(1_048_576, end - start + 1); // max 1 Mo par tranche
            region = new ResourceRegion(resource, start, rangeLength);
        } else {
            long rangeLength = Math.min(1_048_576, contentLength);
            region = new ResourceRegion(resource, 0, rangeLength);
        }

        // 3️⃣ Détecte le vrai Content-Type (audio/m4a, audio/mp3…)
        String mimeType = Files.probeContentType(path);
        MediaType mediaType = (mimeType != null)
                ? MediaType.parseMediaType(mimeType)
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .contentType(mediaType)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(region);
    }
}

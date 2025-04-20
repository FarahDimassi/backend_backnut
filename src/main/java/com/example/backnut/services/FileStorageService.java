package com.example.backnut.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(
            @Value("${app.upload.dir:uploads}") String uploadDir
    ) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de créer le répertoire uploads.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original == null || original.isBlank()) {
            throw new RuntimeException("Le nom de fichier est invalide (vide).");
        }
        // Nettoyage (suppression des chemins relatifs ..)
        String cleaned = StringUtils.cleanPath(original);
        if (cleaned.contains("..")) {
            throw new RuntimeException("Nom de fichier invalide : " + cleaned);
        }

        // Génération d'un nom unique
        String extension = "";
        int dotIndex = cleaned.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = cleaned.substring(dotIndex);
        }
        String filename = UUID.randomUUID().toString() + extension;

        try {
            Path target = this.uploadDir.resolve(filename);
            // Veille à ce que le dossier parent existe
            Files.createDirectories(target.getParent());
            // Copie du contenu en écrasant si besoin
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException ex) {
            throw new RuntimeException("Erreur lors de l’enregistrement du fichier " + filename, ex);
        }
    }

    public Path loadFile(String filename) {
        return uploadDir.resolve(filename).normalize();
    }
}

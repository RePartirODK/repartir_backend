package com.example.repartir_backend.services;

import com.example.repartir_backend.enumerations.TypeFichier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class UploadService {
    @Value("${file.upload-dir:${user.home}/Desktop/uploads}")
    private String baseUploadDir;
    @Value("${server.url}")
    private String serverUrl;
    public String uploadFile(MultipartFile file, String fileName, TypeFichier typefichier){
        try{
            //recupérer le fichier le dossier cible en fonction du type de fichier
            Path directory = Paths.get(baseUploadDir, getFolderName(typefichier));

            //creation du dossier
            Files.createDirectories(directory);

            String extension;
            if(typefichier == TypeFichier.PHOTO)
            {
                extension = getFileExtension(file.getOriginalFilename()).orElse("");
            }
            else {
                extension = ".pdf";
            }

            Path filePath = directory.resolve(fileName + extension);

            // Sauvegarde
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            // Construire l'URL HTTP au lieu du chemin local
            String folderName = getFolderName(typefichier); // "photos" ou "autres"
            String relativePath = folderName + "/" + fileName + extension;
            return serverUrl + "/uploads/" + relativePath;
            // Retourne "http://localhost:8183/uploads/photos/user_14.jpg"
        }catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier", e);
        }
    }


    private String getFolderName(TypeFichier typeFichier) {
        return switch (typeFichier)
        {
            case PHOTO -> "photos";
            case AUTRE -> "autres";
        };
    }
    private Optional<String> getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return Optional.of(filename.substring(filename.lastIndexOf(".")));
        }
        return Optional.empty();
    }
}

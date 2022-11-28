package com.example.backend.services;


import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Service
public class FileService {
    private final Path fileStorageLocation;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get((fileStorageProperties.getUploadDir()+"/eventAttachment")).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Error at File Service constructor.", ex);
        }
    }

    public boolean tryFile(MultipartFile file){
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        System.out.println("tryFile : " + fileName);
        if (fileName.contains("..") || fileName.contains("/")) {
            throw new RuntimeException("Error. Filename contains unappropriate characters.");
        }
        if (file.getSize()>10000000) {
            throw new RuntimeException("The attachment is too big!");
        }
        String[] fileNameSplit = file.getOriginalFilename().split("[.]");
        if (fileNameSplit.length>2){
            throw new RuntimeException("Filename contains '.'.");
        }
        System.out.println(file.getSize());
        return true;
    }

    public String store(MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        tryFile(file);
        String[] fileNameSplit = file.getOriginalFilename().split("[.]");
        long timestamp = (new Date()).getTime();
        String newFileName = fileNameSplit[0] + "-" + timestamp + "." + fileNameSplit[1];
        try {
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Can not store file. Please try again.", ex);
        }
    }

    public Resource loadFileAsResource(String fileName){
        try{
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            System.out.println(filePath);
            Resource resource = new UrlResource(filePath.toUri());
            System.out.println(resource);
            if(resource.exists()){
//                resource.getFile().delete();
                return resource;
            } else {
                throw new RuntimeException("File not found" + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found in catch " + fileName, ex);
        } catch (IOException ex) {
            throw new RuntimeException("File can not delete " + fileName, ex);
        }
    }

    public void deleteFile(String fileName){
        try{
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            System.out.println(filePath);
            Resource resource = new UrlResource(filePath.toUri());
            System.out.println(resource);
            if(resource.exists()){
                resource.getFile().delete();
            } else {
                throw new RuntimeException("File not found" + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found in catch " + fileName, ex);
        } catch (IOException ex) {
            throw new RuntimeException("File can not delete " + fileName, ex);
        }
    }

}

package com.example.backend.controllers;

import com.example.backend.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@CrossOrigin
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService){
        this.fileService = fileService;
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("serveFile");
        Resource file = fileService.loadFileAsResource(filename);
        System.out.println(file.getDescription());
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(file);
        String contentType = null;
        try{
            contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException e) {
//            throw new RuntimeException();
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename()).body(file);
    }

    @PostMapping("")
    public String fileUpload(@RequestParam("file")MultipartFile file, HttpServletRequest request, HttpServletResponse response){
//        fileService.store(file, 2, "jpg");
//        String[] fileNameSplit = file.getOriginalFilename().split("[.]");
//        System.out.println(fileNameSplit.length);
//        String fileSurname = fileNameSplit[1];
//        System.out.println(fileSurname);
//        return fileService.store(file, 2, "jpg");
        return fileService.store(file, request, response);
    }

    @GetMapping("")
    public boolean testFile(@RequestParam("file")MultipartFile file){
        return fileService.tryFile(file);
    }

}

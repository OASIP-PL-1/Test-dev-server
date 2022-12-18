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
// @CrossOrigin(origins = "*")
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
        Resource resource = fileService.loadFileAsResource(filename, request, response);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {

        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename()).body(resource);
    }

    @PostMapping("")
    public String fileUpload(@RequestParam("file")MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        return fileService.store(file, request, response);
    }

    @DeleteMapping("/{filename:.+}")
    public void deleteFile(@PathVariable String filename, HttpServletRequest request, HttpServletResponse response){
        fileService.deleteFile(filename);
    }


}

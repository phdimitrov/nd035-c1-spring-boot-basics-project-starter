package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    private final UserService userService;
    private final FileService fileService;

    public FileController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<Resource> getFile(Authentication authentication, @PathVariable("fileId") Integer fileId) {
        String username = (String) authentication.getPrincipal();
        User user = userService.getUser(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        File file = fileService.getFile(fileId);
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName());
        headers.add(HttpHeaders.CONTENT_TYPE, file.getContentType());
        headers.add(HttpHeaders.CONTENT_LENGTH, file.getFileSize());
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(file.getFileData()));
    }

    @GetMapping("/delete/files/{fileId}")
    public String deleteFileByGet(@PathVariable("fileId") Integer fileId) {
        fileService.deleteFile(fileId);
        return "redirect:/result?success=Deleted";
    }

    @PostMapping("/files")
    public String fileUpload(Authentication authentication, @RequestParam("fileUpload") MultipartFile file) {

        if (file.getSize() >= 10485760) {
            return "redirect:/result?error=" + URLEncoder
                    .encode("File size must be less than 10MB.", StandardCharsets.UTF_8);
        }

        User user = userService.getAuthenticatedUser(authentication);

        try {

            String fileName = file.getOriginalFilename();
            Integer userId = user.getUserId();

            final boolean available = fileService.isFileNameAvailable(fileName, userId);
            if (!available) {
                return "redirect:/result?error=" + URLEncoder.encode("Name already exists.", StandardCharsets.UTF_8);
            }

            File newFile = new File(null, fileName, file.getContentType(),
                    String.valueOf(file.getSize()), userId, file.getBytes());

            fileService.saveFile(newFile);
        } catch (IOException e) {
            LOGGER.error("Uploading file", e);
            return "redirect:/result?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }

        return "redirect:/result?success=Saved";
    }

}

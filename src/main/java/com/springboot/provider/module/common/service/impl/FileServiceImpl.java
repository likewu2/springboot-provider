package com.springboot.provider.module.common.service.impl;

import com.springboot.provider.module.common.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileServiceImpl implements FileService {

    @Value("${spring.servlet.multipart.location}")
    private String location;


    @Override
    public Map<String, Path> uploadSingle(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (file.isEmpty() || fileName.contains("..")) {
            return null;
        }

        Map<String, Path> map = new HashMap<>();
        // Get the file and save it somewhere
        Path path = Paths.get(location + fileName);
        try {
            // write
            Files.write(path, file.getBytes());

            // copy
//            InputStream inputStream = file.getInputStream();
//            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
//            inputStream.close();

            map.put(fileName, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Path> uploadMultipart(MultipartFile[] files) {
        for (MultipartFile file : files) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            if (file.isEmpty() || fileName.contains("..")) {
                return null;
            }
        }

        Map<String, Path> map = new HashMap<>();
        for (MultipartFile file : files) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            // Get the file and save it somewhere
            Path path = Paths.get(location + fileName);
            try {
                // write
                Files.write(path, file.getBytes());

                // copy
//                InputStream inputStream = file.getInputStream();
//                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
//                inputStream.close();

                map.put(fileName, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public Map<String, Path> list() {
        Map<String, Path> map = new HashMap<>();
        Stream<Path> stream = null;
        try {
            stream = Files.list(Paths.get(location));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert stream != null;
        stream.forEach(file -> {
            String s = file.getFileName().toString();
            map.put(s, file.getFileName());
        });
        return map;
    }

    @Override
    public ResponseEntity<byte[]> download(String filename) throws IOException {
        if (!StringUtils.hasText(filename) || filename.contains("..")) {
            return null;
        }
        File file = new File(location + File.separator + filename);
        HttpHeaders headers = new HttpHeaders();
        filename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(Files.readAllBytes(file.toPath()), headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String filename) throws MalformedURLException {
        if (!StringUtils.hasText(filename) || filename.contains("..")) {
            return null;
        }
        Path path = Paths.get(location + filename);
        Resource resource = new UrlResource(path.toUri());

        filename = new String(Objects.requireNonNull(resource.getFilename()).getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + filename + "\"")
                .body(resource);
    }
}

package com.springboot.provider.module.common.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface FileService {
    Map<String, Path> uploadSingle(MultipartFile file);
    Map<String, Path> uploadMultipart(MultipartFile[] files);
    Map<String, Path> list();
    ResponseEntity<byte[]> download(String filename) throws IOException;
}

package com.pwdk.grocereach.image.applications;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CloudinaryService {
  String uploadFile(MultipartFile file, String folderName);
}

package com.pwdk.grocereach.image.presentations.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {
  private UUID productID;
  private MultipartFile[] multipartFiles;
}

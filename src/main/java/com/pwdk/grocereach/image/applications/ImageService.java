package com.pwdk.grocereach.image.applications;

import com.pwdk.grocereach.product.presentations.dtos.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageService {
  ProductImageResponse  uploadSingleImage(MultipartFile file, UUID productID, boolean isPrimary);
}

package com.pwdk.grocereach.image.applications;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.pwdk.grocereach.product.presentations.dtos.ProductImageResponse;

public interface ImageService {
  ProductImageResponse  uploadSingleImage(MultipartFile file, UUID productID, boolean isPrimary);
  List<ProductImageResponse> uploadMultiImage(MultipartFile[] files, UUID uuid, boolean isPrimary);
  void softDeleteImage(UUID imageId);
}

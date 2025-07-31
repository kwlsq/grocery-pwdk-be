package com.pwdk.grocereach.image.applications.impl;

import com.pwdk.grocereach.image.applications.CloudinaryService;
import com.pwdk.grocereach.image.applications.ImageService;
import com.pwdk.grocereach.image.domains.entities.ProductImages;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.presentations.dtos.ProductImageResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ImageServiceImplementation implements ImageService {

  private final ProductRepository productRepository;
  private final CloudinaryService cloudinaryService;

  public ImageServiceImplementation(ProductRepository productRepository, CloudinaryService cloudinaryService) {
    this.productRepository = productRepository;
    this.cloudinaryService = cloudinaryService;
  }

  @Override
  public ProductImageResponse uploadSingleImage(MultipartFile file, UUID productID, boolean isPrimary) {
    Product product = productRepository.findById(productID).orElseThrow(() -> new RuntimeException("Product not found!"));

    String url = cloudinaryService.uploadFile(file, "thumbnail-image");

    ProductImages image = ProductImages.builder()
        .product(product)
        .imageUrl(url)
        .isPrimary(isPrimary)
        .build();
  }
}

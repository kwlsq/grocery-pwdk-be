package com.pwdk.grocereach.image.applications.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pwdk.grocereach.common.exception.ProductNotFoundException;
import com.pwdk.grocereach.image.applications.CloudinaryService;
import com.pwdk.grocereach.image.applications.ImageService;
import com.pwdk.grocereach.image.domains.entities.ProductImages;
import com.pwdk.grocereach.image.infrastructures.repositories.ProductImagesRepository;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.presentations.dtos.ProductImageResponse;

@Service
public class ImageServiceImplementation implements ImageService {

  private final ProductRepository productRepository;
  private final CloudinaryService cloudinaryService;
  private final ProductImagesRepository productImagesRepository;

  public ImageServiceImplementation(ProductRepository productRepository, CloudinaryService cloudinaryService, ProductImagesRepository productImagesRepository) {
    this.productRepository = productRepository;
    this.cloudinaryService = cloudinaryService;
    this.productImagesRepository = productImagesRepository;
  }

  @Override
  public ProductImageResponse uploadSingleImage(MultipartFile file, UUID productID, boolean isPrimary) {
    Product product = productRepository.findById(productID).orElseThrow(() -> new ProductNotFoundException("Product not found!"));

    String url = cloudinaryService.uploadFile(file, "product-image");

    ProductImages image = ProductImages.builder()
        .product(product)
        .imageUrl(url)
        .isPrimary(isPrimary)
        .build();

    productImagesRepository.save(image);

    return ProductImageResponse.from(image);
  }

  @Override
  public List<ProductImageResponse> uploadMultiImage(MultipartFile[] files, UUID productID, boolean isPrimary) {
    if (files.length < 1) {
      throw  new RuntimeException("There are no file to upload!");
    }

    Product product = productRepository.findById(productID).orElseThrow(() -> new ProductNotFoundException("Product not found!"));

    List<ProductImageResponse> productImageResponseList = new ArrayList<>();

    for (MultipartFile file : files) {
      String url = cloudinaryService.uploadFile(file, "product-image");

      ProductImages image = ProductImages.builder()
          .product(product)
          .imageUrl(url)
          .isPrimary(isPrimary)
          .build();

      productImagesRepository.save(image);
      productImageResponseList.add(ProductImageResponse.from(image));
    }

    return productImageResponseList;
  }

  @Override
  public void softDeleteImage(UUID imageId) {
    ProductImages image = productImagesRepository.findById(imageId)
        .orElseThrow(() -> new RuntimeException("Image not found!"));
    image.setDeletedAt(java.time.Instant.now());
    productImagesRepository.save(image);
  }
}

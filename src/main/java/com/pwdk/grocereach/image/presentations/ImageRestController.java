package com.pwdk.grocereach.image.presentations;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.image.applications.ImageService;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

  private final ImageService imageService;

  public ImageRestController(ImageService imageService) {
    this.imageService = imageService;
  }

  @PostMapping("/upload-single")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> uploadSingleImage(@RequestParam("file") MultipartFile file,
                                             @RequestParam("productID") String productID,
                                             @RequestParam("isPrimary") boolean isPrimary
  ) {
    UUID productUUID = UUID.fromString(productID);
    return Response.successfulResponse(
        "Successfully to upload image!",
        imageService.uploadSingleImage(file, productUUID, isPrimary)
    );
  }

  @PostMapping("/upload-multi")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> uploadMultiImage(@RequestPart("file") MultipartFile[] file,
                                             @RequestParam("productID") String productID,
                                             @RequestParam("isPrimary") boolean isPrimary
  ) {
    UUID productUUID = UUID.fromString(productID);
    return Response.successfulResponse(
        "Successfully to upload image!",
        imageService.uploadMultiImage(file, productUUID, isPrimary)
    );
  }

  @DeleteMapping("/{imageId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteImage(@PathVariable String imageId) {
    UUID uuid = UUID.fromString(imageId);
    imageService.softDeleteImage(uuid);
    return Response.successfulResponse("Image deleted successfully");
  }
}

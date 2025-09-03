package com.pwdk.grocereach.image.presentations;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.image.applications.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

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
}

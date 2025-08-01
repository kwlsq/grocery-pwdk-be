package com.pwdk.grocereach.image.applications.impl;

import com.cloudinary.Cloudinary;
import com.pwdk.grocereach.image.applications.CloudinaryService;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryServiceImplementation implements CloudinaryService {

  private final Cloudinary cloudinary;

  public CloudinaryServiceImplementation(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

  @Override
  public String uploadFile(MultipartFile file, String folderName) {
    try {
      HashMap<Object, Object> options = new HashMap<>();
      options.put("folder", folderName);
      Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
      String publicID = (String) uploadedFile.get("public_id");
      return cloudinary.url().secure(true).generate(publicID);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}

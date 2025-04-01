package com.walmart.demo.service;

import com.google.cloud.storage.*;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GoogleCloudStorageService {

    private static final String BUCKET_NAME = "walmart-demo-proj";
    private final Storage storage;

    public GoogleCloudStorageService(Storage storage) {
        this.storage = storage; // Injecting Storage instance
    }

    @SuppressWarnings("deprecation")
	public String uploadImage(MultipartFile file) {
        String fileName = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        try {
            storage.create(blobInfo, file.getInputStream());
            return String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to GCS", e);
        }
    }

    public List<String> getSimilarProducts(MultipartFile image) {
        try (ImageAnnotatorClient visionClient = ImageAnnotatorClient.create()) {
            ByteString imgBytes = ByteString.copyFrom(image.getBytes());

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feature = Feature.newBuilder().setType(Feature.Type.PRODUCT_SEARCH).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feature)
                    .setImage(img)
                    .build();

            AnnotateImageResponse response = visionClient.batchAnnotateImages(List.of(request)).getResponses(0);

            return response.getProductSearchResults().getResultsList().stream()
                    .map(result -> result.getProduct().getName())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Error using Google Vision API", e);
        }
    }
}

package com.walmart.demo.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleVisionService {

    public List<String> getSimilarProducts(MultipartFile image) {
        try (ImageAnnotatorClient visionClient = ImageAnnotatorClient.create()) {
            ByteString imgBytes = ByteString.copyFrom(image.getBytes());

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feature = Feature.newBuilder().setType(Feature.Type.PRODUCT_SEARCH).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feature)
                    .setImage(img)
                    .build();

            List<AnnotateImageResponse> responses = visionClient.batchAnnotateImages(List.of(request)).getResponsesList();

            return responses.stream()
            	    .flatMap(resp -> resp.getProductSearchResults().getResultsList().stream())
            	    .map(result -> result.getProduct().getName())  // Use getProduct() instead of getImage().getSource().getImageUri()
            	    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Error using Google Vision API", e);
        }
    }
}

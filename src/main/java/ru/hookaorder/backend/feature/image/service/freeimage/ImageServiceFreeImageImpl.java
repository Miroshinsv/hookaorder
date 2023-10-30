package ru.hookaorder.backend.feature.image.service.freeimage;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hookaorder.backend.feature.image.service.ImageService;
import ru.hookaorder.backend.utils.HTTPClientUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class ImageServiceFreeImageImpl implements ImageService {

    private static final String ERROR_NULL_VALUE = null;
    @Value("${free.image.api.key}")
    private String freeImageAPIKey;
    private static final String FREE_IMAGE_API_URL = "https://freeimage.host/api/1/upload";

    @Override
    public String uploadImage(String base64EncodedImage) {
        String geocodeRequest = FREE_IMAGE_API_URL +
            "&key=" + freeImageAPIKey + "&source=" + base64EncodedImage + "&format=json";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(geocodeRequest))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
            HttpResponse<String> response = HTTPClientUtils.sendRequest(request);
            return parseJsonFreeImageResponse(response.body());
        } catch (URISyntaxException e) {
            log.error("Error in HTTP Request URI Syntax: " + geocodeRequest, e);
            return ERROR_NULL_VALUE;
        } catch (JsonProcessingException e) {
            log.error("Error in Free Image JSON Response parse", e);
            return ERROR_NULL_VALUE;
        } catch (IOException | InterruptedException e) {
            log.error("Error in HTTP Request sending", e);
            return ERROR_NULL_VALUE;
        } catch (Exception e) {
            log.error("Error in HTTP Request sending or Response parsing", e);
            return ERROR_NULL_VALUE;
        }
    }

    private String parseJsonFreeImageResponse(String body) {
        JSONObject obj = new JSONObject(body);
        return obj.getJSONObject("image").getString("url");
    }
}

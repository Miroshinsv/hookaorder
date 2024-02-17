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
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class ImageServiceImgBbImpl implements ImageService {

    private static final String ERROR_NULL_VALUE = null;
    @Value("${img.bb.api.key}")
    private String imgBbAPIKey;
    private static final String IMGBB_API_URL = "https://api.imgbb.com/1/upload";

    @Override
    public String uploadImage(String base64EncodedImage) {
        String geocodeRequest = IMGBB_API_URL + "?key=" + imgBbAPIKey;
        try {
            Map<String, String> formData = Map.of("image", base64EncodedImage);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(geocodeRequest))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(formData)))
                .build();
            HttpResponse<String> response = HTTPClientUtils.sendRequest(request);
            return parseJsonImgBbResponse(response.body());
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

    private String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }

    private String parseJsonImgBbResponse(String body) {
        JSONObject obj = new JSONObject(body);
        return obj.getJSONObject("data").getJSONObject("image").getString("url");
    }
}

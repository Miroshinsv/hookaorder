package ru.hookaorder.backend.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class HTTPClientUtils {

    private HTTPClientUtils() {
    }

    public static HttpRequest buildRequestWithURI(String uri) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(uri))
                .GET()
                .build();
    }

    public static HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient
                .newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }
}

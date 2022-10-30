package ru.hookaorder.backend.services.address.ya;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.hookaorder.backend.dto.address.ya.AddressYaDTO;
import ru.hookaorder.backend.dto.address.ya.FeatureMember;
import ru.hookaorder.backend.dto.address.ya.GeocoderMetaData;
import ru.hookaorder.backend.dto.address.ya.MetaDataProperty;
import ru.hookaorder.backend.feature.address.entity.AddressEntity;
import ru.hookaorder.backend.services.address.AddressService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AddressServiceYa implements AddressService {
    private static final String YA_MAPS_API_URL = "https://geocode-maps.yandex.ru/1.x/?format=json";
    private static final String POS_SPLIT_REGEX = " ";

    @Override
    public List<AddressEntity> getPossibleAddresses(String addressSearchString, String apiKey) {

        String geocodeRequest = YA_MAPS_API_URL +
                "&apikey=" + apiKey + "&geocode=" + addressSearchString;
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(geocodeRequest))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());

            List<AddressEntity> possibleAddresses = parseJsonYaResponse(jsonResponse);
            return possibleAddresses;

        } catch (URISyntaxException e) {
            log.error("Error in HTTP Request URI Syntax: " + geocodeRequest, e);
            return Collections.emptyList();
        } catch (IOException | InterruptedException e) {
            log.error("Error in HTTP Request sending", e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error in HTTP Response parsing", e);
            return Collections.emptyList();
        }
    }

    private static List<AddressEntity> parseJsonYaResponse(JSONObject jsonResponse) {

        List<AddressEntity> possibleAddresses = new ArrayList<>();

        Gson gson = new GsonBuilder().create();
        AddressYaDTO addressYaData = gson.fromJson(jsonResponse.toString(), AddressYaDTO.class);

        List<FeatureMember> featureMemberList = addressYaData.getResponse()
                .getGeoObjectCollection().getFeatureMember();

        featureMemberList.forEach(featureMember -> {
            MetaDataProperty metaData = featureMember.getGeoObject().getMetaDataProperty();
            GeocoderMetaData geoMetaData = metaData.getGeocoderMetaData();

            String countryCode = geoMetaData.getAddress().getCountryCode();

            String addressText = geoMetaData.getText();

            String geoPositionToParse = featureMember.getGeoObject().getPoint().getPos();
            String[] geoPositionParsed = geoPositionToParse.split(POS_SPLIT_REGEX);
            double lat = Double.parseDouble(geoPositionParsed[0]);
            double lng = Double.parseDouble(geoPositionParsed[1]);

            possibleAddresses.add(new AddressEntity(countryCode, addressText, lat, lng));
        });

        return possibleAddresses;
    }
}

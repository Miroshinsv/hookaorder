package ru.hookaorder.backend.services.address.ya;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hookaorder.backend.dto.address.ya.AddressYaDTO;
import ru.hookaorder.backend.dto.address.ya.FeatureMember;
import ru.hookaorder.backend.dto.address.ya.GeocoderMetaData;
import ru.hookaorder.backend.dto.address.ya.MetaDataProperty;
import ru.hookaorder.backend.feature.address.entity.AddressEntity;
import ru.hookaorder.backend.services.address.AddressService;
import ru.hookaorder.backend.utils.HTTPClientUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AddressServiceYa implements AddressService {
    private static final String YA_MAPS_API_URL = "https://geocode-maps.yandex.ru/1.x/?format=json";
    private static final String POS_SPLIT_REGEX = " ";
    private static final List<AddressEntity> ERROR_LIST_NULL_VALUE = null;

    @Override
    public List<AddressEntity> getPossibleAddresses(String addressSearchString) {

        String geocodeRequest = YA_MAPS_API_URL +
                "&apikey=" + System.getenv("apiKey") + "&geocode=" + addressSearchString;
        try {

            HttpRequest request = HTTPClientUtils.buildRequestWithURI(geocodeRequest);
            HttpResponse<String> response = HTTPClientUtils.sendRequest(request);
            return parseJsonYaResponse(response.body());

        } catch (URISyntaxException e) {
            log.error("Error in HTTP Request URI Syntax: " + geocodeRequest, e);
            return ERROR_LIST_NULL_VALUE;
        } catch (JsonProcessingException e) {
            log.error("Error in Yandex JSON Response parse", e);
            return ERROR_LIST_NULL_VALUE;
        } catch (IOException | InterruptedException e) {
            log.error("Error in HTTP Request sending", e);
            return ERROR_LIST_NULL_VALUE;
        } catch (Exception e) {
            log.error("Error in HTTP Request sending or Response parsing", e);
            return ERROR_LIST_NULL_VALUE;
        }
    }

    private static List<AddressEntity> parseJsonYaResponse(String jsonResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AddressYaDTO addressYaData = mapper
                .readValue(jsonResponse, AddressYaDTO.class);

        List<FeatureMember> featureMemberList = addressYaData.getResponse()
                .getGeoObjectCollection().getFeatureMember();

        List<AddressEntity> possibleAddresses = featureMemberList
                .stream()
                .map(featureMember -> {
                    MetaDataProperty metaData = featureMember.getGeoObject().getMetaDataProperty();
                    GeocoderMetaData geoMetaData = metaData.getGeocoderMetaData();

                    String countryCode = geoMetaData.getAddress().getCountryCode();

                    String addressText = geoMetaData.getText();

                    String geoPositionToParse = featureMember.getGeoObject().getPoint().getPos();
                    String[] geoPositionParsed = geoPositionToParse.split(POS_SPLIT_REGEX);
                    double lat = Double.parseDouble(geoPositionParsed[0]);
                    double lng = Double.parseDouble(geoPositionParsed[1]);

                    return new AddressEntity(countryCode, addressText, lat, lng);
                })
                .collect(Collectors.toList());
        return possibleAddresses;
    }
}

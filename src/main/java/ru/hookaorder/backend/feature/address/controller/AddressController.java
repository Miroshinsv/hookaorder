package ru.hookaorder.backend.feature.address.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hookaorder.backend.feature.address.entity.AddressEntity;
import ru.hookaorder.backend.services.address.AddressService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping(value = "/get")
    ResponseEntity<?> createAddress(@RequestParam @NotBlank @Min(4) String addressQuery) {
        List<AddressEntity> possibleAddresses = addressService.getPossibleAddresses(addressQuery);

        if (Objects.isNull(possibleAddresses)) {
            return ResponseEntity.badRequest().body("Errors for Address Search Request");
        }
        return ResponseEntity.ok().body(possibleAddresses);
    }
}

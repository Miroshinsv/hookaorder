package ru.hookaorder.backend.feature.address.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.address.entity.AddressEntity;
import ru.hookaorder.backend.services.address.AddressService;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping(value = "/get")
    ResponseEntity<?> createAddress(@RequestParam String addressQuery, @RequestParam String apiKey) {
        List<AddressEntity> possibleAddresses = addressService.getPossibleAddresses(addressQuery, apiKey);

        if (possibleAddresses.isEmpty()) {
            return ResponseEntity.badRequest().body("Addresses couldn't be found using the request");
        }
        return ResponseEntity.ok().body(possibleAddresses);
    }
}

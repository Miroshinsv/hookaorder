package ru.hookaorder.backend.services.address;

import ru.hookaorder.backend.feature.address.entity.AddressEntity;

import java.util.List;

public interface AddressService {
    List<AddressEntity> getPossibleAddresses(String address);
}

package ru.hookaorder.backend.feature.address.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hookaorder.backend.feature.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity extends BaseEntity {
    @Column(name = "country")
    private String country;

    @Column(name = "address")
    private String address;

    @Column(name = "lat")
    private double lat;

    @Column(name = "lng")
    private double lng;
}


package ru.hookaorder.backend.dto.address.ya;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class PostalCode {

    @SerializedName("PostalCodeNumber")
    private String postalCodeNumber;

    public String getPostalCodeNumber() {
        return postalCodeNumber;
    }

    public void setPostalCodeNumber(String postalCodeNumber) {
        this.postalCodeNumber = postalCodeNumber;
    }

}

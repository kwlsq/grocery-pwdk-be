package com.pwdk.grocereach.User.Presentation.Dto;

import com.pwdk.grocereach.User.Domain.Entities.Address;
import lombok.Data;

import java.util.UUID;

@Data
public class AddressResponse {
    private UUID id;
    private String label;
    private String recipientName;
    private String phone;
    private String fullAddress;
    private String city;
    private String province;
    private String postalCode;
    private boolean isPrimary;

    public AddressResponse(Address address) {
        this.id = address.getId();
        this.label = address.getLabel();
        this.recipientName = address.getRecipientName();
        this.phone = address.getPhone();
        this.fullAddress = address.getFullAddress();
        if (address.getCity() != null) {
            this.city = address.getCity().getName();
        }
        if (address.getProvince() != null) {
            this.province = address.getProvince().getName();
        }
        this.postalCode = address.getPostalCode();
        this.isPrimary = address.isPrimary();
    }
}
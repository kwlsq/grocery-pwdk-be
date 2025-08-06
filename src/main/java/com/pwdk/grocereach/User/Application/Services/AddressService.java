package com.pwdk.grocereach.User.Application.Services; // Or your correct services package

import com.pwdk.grocereach.User.Presentation.Dto.AddressRequest;
import com.pwdk.grocereach.User.Presentation.Dto.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    List<AddressResponse> getUserAddresses(String userEmail);
    AddressResponse createAddress(String userEmail, AddressRequest addressRequest);
    AddressResponse updateAddress(String userEmail, UUID addressId, AddressRequest addressRequest);
    void deleteAddress(String userEmail, UUID addressId);
}
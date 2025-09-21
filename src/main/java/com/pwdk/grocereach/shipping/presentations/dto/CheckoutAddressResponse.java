package com.pwdk.grocereach.shipping.presentations.dto;

import com.pwdk.grocereach.User.Presentation.Dto.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutAddressResponse {
    private List<AddressResponse> addresses;
    private AddressResponse primaryAddress;
    private boolean hasAddresses;
}
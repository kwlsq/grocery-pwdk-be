package com.pwdk.grocereach.User.Application.Implements;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.User.Application.Services.AddressService;
import com.pwdk.grocereach.User.Domain.Entities.Address;
import com.pwdk.grocereach.location.domains.entities.City;
import com.pwdk.grocereach.location.domains.entities.Province;
import com.pwdk.grocereach.location.infrastructures.repositories.CityRepository;
import com.pwdk.grocereach.location.infrastructures.repositories.ProvinceRepository;
import com.pwdk.grocereach.User.Infrastructure.Repositories.AddressRepository;
import com.pwdk.grocereach.User.Presentation.Dto.AddressRequest;
import com.pwdk.grocereach.User.Presentation.Dto.AddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;

    @Override
    public List<AddressResponse> getUserAddresses(String userEmail) {
        User user = findUserByEmail(userEmail);
        return addressRepository.findByUser(user).stream()
                .map(AddressResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse createAddress(String userEmail, AddressRequest request) {
        User user = findUserByEmail(userEmail);
        List<Address> existingAddresses = addressRepository.findByUser(user);
        if (existingAddresses.isEmpty()) {
            request.setPrimary(true);
        }
        if (request.isPrimary()) {
            addressRepository.unsetAllPrimaryAddressesForUser(user);
        }
        Address newAddress = new Address();
        mapRequestToEntity(request, newAddress);
        newAddress.setUser(user);
        Address savedAddress = addressRepository.save(newAddress);
        return new AddressResponse(savedAddress);
    }

    @Override
    public AddressResponse updateAddress(String userEmail, UUID addressId, AddressRequest request) {
        User user = findUserByEmail(userEmail);
        Address address = findAddressByIdAndUser(addressId, user);

        if (request.isPrimary() && !address.isPrimary()) {
            addressRepository.unsetAllPrimaryAddressesForUser(user);
        }

        mapRequestToEntity(request, address);
        Address updatedAddress = addressRepository.save(address);
        return new AddressResponse(updatedAddress);
    }

    @Override
    public void deleteAddress(String userEmail, UUID addressId) {
        User user = findUserByEmail(userEmail);
        Address address = findAddressByIdAndUser(addressId, user);
        addressRepository.delete(address);
    }

    private void mapRequestToEntity(AddressRequest request, Address address) {
        Province province = provinceRepository.findById(request.getProvinceId())
                .orElseThrow(() -> new RuntimeException("Province not found"));
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));

        address.setLabel(request.getLabel());
        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getPhone());
        address.setFullAddress(request.getFullAddress());
        address.setProvince(province);
        address.setCity(city);
        address.setPostalCode(request.getPostalCode());
        address.setPrimary(request.isPrimary());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Address findAddressByIdAndUser(UUID addressId, User user) {
        return addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user"));
    }
}


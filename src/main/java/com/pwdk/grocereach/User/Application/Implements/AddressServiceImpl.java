package com.pwdk.grocereach.User.Application.Implements;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.User.Application.Services.AddressService;
import com.pwdk.grocereach.User.Domain.Entities.Address;
import com.pwdk.grocereach.User.Infrastructure.Repositories.AddressRepository;
import com.pwdk.grocereach.User.Presentation.Dto.AddressRequest;
import com.pwdk.grocereach.User.Presentation.Dto.AddressResponse;
import com.pwdk.grocereach.location.domains.entities.City;
import com.pwdk.grocereach.location.domains.entities.Province;
import com.pwdk.grocereach.location.infrastructures.repositories.CityRepository;
import com.pwdk.grocereach.location.infrastructures.repositories.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // Ensures all database operations in a method happen in one transaction
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

        if (request.isPrimary()) {
            unsetOtherPrimaryAddresses(user);
        }

        Address newAddress = new Address();
        mapRequestToEntity(request, newAddress); // This method is now updated
        newAddress.setUser(user);

        Address savedAddress = addressRepository.save(newAddress);
        return new AddressResponse(savedAddress);
    }

    @Override
    public AddressResponse updateAddress(String userEmail, UUID addressId, AddressRequest request) {
        User user = findUserByEmail(userEmail);
        Address address = findAddressByIdAndUser(addressId, user);

        if (request.isPrimary() && !address.isPrimary()) {
            unsetOtherPrimaryAddresses(user);
        }

        mapRequestToEntity(request, address); // This method is now updated
        Address updatedAddress = addressRepository.save(address);
        return new AddressResponse(updatedAddress);
    }

    @Override
    public void deleteAddress(String userEmail, UUID addressId) {
        User user = findUserByEmail(userEmail);
        Address address = findAddressByIdAndUser(addressId, user);
        addressRepository.delete(address);
    }

    // --- Helper Methods ---

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Address findAddressByIdAndUser(UUID addressId, User user) {
        return addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user"));
    }

    private void unsetOtherPrimaryAddresses(User user) {
        addressRepository.findByUserAndIsPrimaryTrue(user).ifPresent(primaryAddress -> {
            primaryAddress.setPrimary(false);
            addressRepository.save(primaryAddress);
        });
    }

    // --- THIS ENTIRE METHOD IS UPDATED ---
    private void mapRequestToEntity(AddressRequest request, Address address) {
        // Find the Province and City objects from the database using the IDs from the request
        Province province = provinceRepository.findById(request.getProvinceId())
                .orElseThrow(() -> new RuntimeException("Province not found"));
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));
        System.out.println(request.isPrimary());
        address.setLabel(request.getLabel());
        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getPhone());
        address.setFullAddress(request.getFullAddress());
        address.setProvince(province); // Set the Province object
        address.setCity(city);         // Set the City object
        address.setPostalCode(request.getPostalCode());
        address.setPrimary(request.isPrimary());
    }
}

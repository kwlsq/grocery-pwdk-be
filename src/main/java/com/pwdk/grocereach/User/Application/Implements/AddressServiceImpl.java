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

import java.time.LocalDateTime;
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
        return addressRepository.findByUserAndDeletedAtIsNull(user).stream()
                .map(AddressResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse createAddress(String userEmail, AddressRequest request) {
        User user = findUserByEmail(userEmail);
        List<Address> existingAddresses = addressRepository.findByUserAndDeletedAtIsNull(user);
        if (existingAddresses.isEmpty()) {
            request.setPrimary(true);
        }
        if (request.isPrimary()) {
            unsetAllPrimaryAddressesForUser(user);
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
        Address address = findActiveAddressByIdAndUser(addressId, user);

        if (request.isPrimary() && !address.isPrimary()) {
            unsetAllPrimaryAddressesForUser(user);
        }

        mapRequestToEntity(request, address);
        Address updatedAddress = addressRepository.save(address);
        return new AddressResponse(updatedAddress);
    }

    @Override
    public void deleteAddress(String userEmail, UUID addressId) {
        User user = findUserByEmail(userEmail);
        Address address = findActiveAddressByIdAndUser(addressId, user);

        address.setDeletedAt(LocalDateTime.now());
        addressRepository.save(address);

        if (address.isPrimary()) {
            setNewPrimaryAddressIfNeeded(user);
        }
    }

    private void setNewPrimaryAddressIfNeeded(User user) {
        List<Address> activeAddresses = addressRepository.findByUserAndDeletedAtIsNull(user);

        if (!activeAddresses.isEmpty()) {
            Address newPrimaryAddress = activeAddresses.get(0);
            newPrimaryAddress.setPrimary(true);
            addressRepository.save(newPrimaryAddress);
        }
    }

    private void unsetAllPrimaryAddressesForUser(User user) {
        List<Address> activeAddresses = addressRepository.findByUserAndDeletedAtIsNull(user);
        activeAddresses.forEach(address -> address.setPrimary(false));
        addressRepository.saveAll(activeAddresses);
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

    private Address findActiveAddressByIdAndUser(UUID addressId, User user) {
        return addressRepository.findByIdAndUserAndDeletedAtIsNull(addressId, user)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user"));
    }
}
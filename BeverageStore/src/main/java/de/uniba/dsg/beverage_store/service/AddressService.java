package de.uniba.dsg.beverage_store.service;

import de.uniba.dsg.beverage_store.dto.AddressDTO;
import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.model.Address;
import de.uniba.dsg.beverage_store.model.User;
import de.uniba.dsg.beverage_store.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final UserService userService;
    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(UserService userService, AddressRepository addressRepository) {
        this.userService = userService;
        this.addressRepository = addressRepository;
    }

    public Address getAddressById(Long id) throws NotFoundException {
        Optional<Address> addressOptional = addressRepository.findById(id);

        if (addressOptional.isEmpty()) {
            throw new NotFoundException("No Address found with the ID: "+ id);
        }

        return addressOptional.get();
    }

    public List<Address> getAllByUsername(String username) {
        return addressRepository.findAllByUserUsername(username);
    }

    public Address addAddress(AddressDTO addressDTO, String username) throws NotFoundException {
        User user = userService.getUserByUserName(username);

        Address address = new Address(null, addressDTO.getName(), addressDTO.getStreet(), addressDTO.getHouseNumber(), addressDTO.getPostalCode(), user, null, null);
        addressRepository.save(address);

        return address;
    }

    public void updateAddress(Long addressId, AddressDTO addressDTO) throws NotFoundException {
        Address address = getAddressById(addressId);

        address.setName(addressDTO.getName());
        address.setStreet(addressDTO.getStreet());
        address.setHouseNumber(addressDTO.getHouseNumber());
        address.setPostalCode(addressDTO.getPostalCode());

        addressRepository.save(address);
    }
}

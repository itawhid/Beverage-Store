package de.uniba.dsg.beverage_store.service;

import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.model.Address;
import de.uniba.dsg.beverage_store.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
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
}

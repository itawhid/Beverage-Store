package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.model.dto.AddressDTO;
import de.uniba.dsg.beverage_store.model.db.Address;
import de.uniba.dsg.beverage_store.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(value = "/address")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public String getAddresses(Model model, Principal principal) {
        List<Address> addresses = addressService.getAllByUsername(principal.getName());

        model.addAttribute("addresses", addresses);
        model.addAttribute("addressDTO", new AddressDTO());

        return "address/list";
    }

    @PostMapping
    public String createAddress(@Valid AddressDTO addressDTO, Errors errors, Model model, Principal principal) {
        boolean hasModelError = false, hasServerError = false;

        if (errors.hasErrors()) {
            hasModelError = true;
        }

        if (!hasModelError) {
            try {
                addressService.addAddress(addressDTO, principal.getName());
            } catch (Exception e) {
                hasServerError = true;
            }
        }

        if (hasModelError || hasServerError) {
            List<Address> addresses = addressService.getAllByUsername(principal.getName());

            model.addAttribute("addresses", addresses);
            model.addAttribute("hasServerError", hasServerError);

            return "address/list";
        }

        return "redirect:/address";
    }

    @GetMapping("/edit/{id}")
    public String getEditAddress(@PathVariable("id") long id, Model model) {
        try {
            Address address = addressService.getAddressById(id);

            model.addAttribute("addressId", address.getId());
            model.addAttribute("addressDTO", new AddressDTO(address.getName(), address.getStreet(), address.getHouseNumber(), address.getPostalCode()));
        } catch (Exception e) {
            model.addAttribute("addressDTO", new Address());
            model.addAttribute("hasServerError", true);
        }

        return "address/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateAddress(@PathVariable("id") long id, @Valid AddressDTO addressDTO, Errors errors, Model model) {
        boolean hasModelError = false, hasServerError = false;

        if (errors.hasErrors()) {
            hasModelError = true;
        }

        if (!hasModelError) {
            try {
                addressService.updateAddress(id, addressDTO);
            } catch (Exception e) {
                hasServerError = true;
            }
        }

        if (hasModelError || hasServerError) {
            model.addAttribute("addressId", id);
            model.addAttribute("hasServerError", hasServerError);

            return "address/edit";
        }

        return "redirect:/address";
    }
}

package de.uniba.dsg.beverage_store.service;

import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.model.BeverageType;
import de.uniba.dsg.beverage_store.model.Bottle;
import de.uniba.dsg.beverage_store.model.CartItem;
import de.uniba.dsg.beverage_store.model.Crate;
import de.uniba.dsg.beverage_store.properties.BottleProperties;
import de.uniba.dsg.beverage_store.properties.CrateProperties;
import de.uniba.dsg.beverage_store.repository.BottleRepository;
import de.uniba.dsg.beverage_store.repository.CrateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class BeverageService {

    private final CrateRepository crateRepository;
    private final BottleRepository bottleRepository;

    private final CrateProperties crateProperties;
    private final BottleProperties bottleProperties;

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

    @Autowired
    public BeverageService(CrateRepository crateRepository,
                           BottleRepository bottleRepository,
                           CrateProperties crateProperties,
                           BottleProperties bottleProperties) {
        this.crateRepository = crateRepository;
        this.bottleRepository = bottleRepository;

        this.crateProperties = crateProperties;
        this.bottleProperties = bottleProperties;
    }

    public Bottle getBottleById(Long id) throws NotFoundException {
        Optional<Bottle> bottleOptional = bottleRepository.findById(id);

        if (bottleOptional.isEmpty()) {
            throw new NotFoundException("No Bottle found with ID: " + id);
        }

        return bottleOptional.get();
    }

    public Page<Bottle> getPagedBottlesWithAllowedStock(int page) {
        Page<Bottle> bottlePage = bottleRepository.findByOrderByNameAsc(PageRequest.of(page - 1, bottleProperties.getPageSize()));

        for (Bottle bottle : bottlePage.getContent()) {
            bottle.setAllowedInStockToInStock();

            Optional<CartItem> cartItemOptional = cartService.getCartItems()
                    .stream()
                    .filter(x -> x.getBeverageType() == BeverageType.BOTTLE && x.getBeverageId() == bottle.getId())
                    .findFirst();

            cartItemOptional.ifPresent(cartItem -> bottle.decreaseAllowedInStock(cartItem.getQuantity()));
        }

        return bottlePage;
    }

    public Crate getCrateById(Long id) throws NotFoundException {
        Optional<Crate> crateOptional = crateRepository.findById(id);

        if (crateOptional.isEmpty()) {
            throw new NotFoundException("No Crate found with ID: " + id);
        }

        return crateOptional.get();
    }

    public Page<Crate> getPagedCratesAllowedStock(int page) {
        Page<Crate> cratePage = crateRepository.findByOrderByNameAsc(PageRequest.of(page - 1, crateProperties.getPageSize()));

        for (Crate crate : cratePage.getContent()) {
            crate.setAllowedInStockToInStock();

            Optional<CartItem> cartItemOptional = cartService.getCartItems()
                    .stream()
                    .filter(x -> x.getBeverageType() == BeverageType.CRATE && x.getBeverageId() == crate.getId())
                    .findFirst();

            cartItemOptional.ifPresent(cartItem -> crate.decreaseAllowedInStock(cartItem.getQuantity()));
        }

        return cratePage;
    }
}

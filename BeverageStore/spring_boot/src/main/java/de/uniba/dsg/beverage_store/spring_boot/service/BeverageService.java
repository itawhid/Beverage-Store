package de.uniba.dsg.beverage_store.spring_boot.service;

import de.uniba.dsg.beverage_store.spring_boot.exception.NotFoundException;
import de.uniba.dsg.beverage_store.spring_boot.model.BeverageType;
import de.uniba.dsg.beverage_store.spring_boot.model.CartItem;
import de.uniba.dsg.beverage_store.spring_boot.model.db.Bottle;
import de.uniba.dsg.beverage_store.spring_boot.model.db.Crate;
import de.uniba.dsg.beverage_store.spring_boot.model.dto.BottleDTO;
import de.uniba.dsg.beverage_store.spring_boot.model.dto.CrateDTO;
import de.uniba.dsg.beverage_store.spring_boot.properties.BottleProperties;
import de.uniba.dsg.beverage_store.spring_boot.properties.CrateProperties;
import de.uniba.dsg.beverage_store.spring_boot.repository.BottleRepository;
import de.uniba.dsg.beverage_store.spring_boot.repository.CrateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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

    public List<Bottle> getBottles() {
        return bottleRepository.findAll();
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

    public Bottle addBottle(BottleDTO bottleDTO) {
        Bottle bottle = new Bottle(
                null,
                bottleDTO.getName(),
                bottleDTO.getPicUrl(),
                bottleDTO.getVolume(),
                bottleDTO.getVolumePercent(),
                bottleDTO.getPrice(),
                bottleDTO.getSupplier(),
                bottleDTO.getInStock(),
                null,
                null
        );

        bottleRepository.save(bottle);

        return bottle;
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

    public Crate addCrate(CrateDTO crateDTO) throws NotFoundException {
        Crate crate = new Crate(
                null,
                crateDTO.getName(),
                crateDTO.getPicUrl(),
                crateDTO.getNoOfBottles(),
                crateDTO.getPrice(),
                crateDTO.getInStock(),
                getBottleById(crateDTO.getBottleId()),
                null
        );

        crateRepository.save(crate);

        return crate;
    }

    public Bottle addStockToBottle(Long id, int quantity) throws NotFoundException {
        Optional<Bottle> optionalBottle = bottleRepository.findById(id);

        if (optionalBottle.isEmpty())
            throw new NotFoundException("Bottle not found with ID: " + id);

        Bottle bottle = optionalBottle.get();
        bottle.setInStock(bottle.getInStock() + quantity);

        bottleRepository.save(bottle);

        return bottle;
    }

    public Crate addStockToCrate(Long id, int quantity) throws NotFoundException {
        Optional<Crate> optionalCrate = crateRepository.findById(id);

        if (optionalCrate.isEmpty())
            throw new NotFoundException("Crate not found with ID: " + id);

        Crate crate = optionalCrate.get();
        crate.setInStock(crate.getInStock() + quantity);

        crateRepository.save(crate);

        return crate;
    }
}

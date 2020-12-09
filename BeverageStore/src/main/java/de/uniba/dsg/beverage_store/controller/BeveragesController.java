package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.model.Bottle;
import de.uniba.dsg.beverage_store.model.Crate;
import de.uniba.dsg.beverage_store.repository.BottleRepository;
import de.uniba.dsg.beverage_store.repository.CrateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/beverages")
public class BeveragesController {

    private final BottleRepository bottleRepository;
    private final CrateRepository crateRepository;

    @Autowired
    public BeveragesController(BottleRepository bottleRepository, CrateRepository crateRepository) {
        this.bottleRepository = bottleRepository;
        this.crateRepository = crateRepository;
    }

    @GetMapping
    public String getBeverages(Model model) {
        List<Bottle> bottles = bottleRepository.findByOrderByNameAsc();
        List<Crate> crates = crateRepository.findByOrderByNameAsc();

        model.addAttribute("bottles", bottles);
        model.addAttribute("crates", crates);

        return "beverages";
    }
}

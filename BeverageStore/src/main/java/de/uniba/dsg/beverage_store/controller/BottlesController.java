package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.model.Bottle;
import de.uniba.dsg.beverage_store.repository.BottleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/bottles")
public class BottlesController {

    private final BottleRepository bottleRepository;

    @Autowired
    public BottlesController(BottleRepository bottleRepository) {
        this.bottleRepository = bottleRepository;
    }

    @GetMapping
    public String getBeverages(Model model) {
        List<Bottle> bottles = bottleRepository.findByOrderByNameAsc();

        model.addAttribute("bottles", bottles);

        return "bottles";
    }
}

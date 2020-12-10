package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.model.Crate;
import de.uniba.dsg.beverage_store.repository.CrateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/crates")
public class CratesController {

    private final CrateRepository crateRepository;

    @Autowired
    public CratesController(CrateRepository crateRepository) {
        this.crateRepository = crateRepository;
    }

    @GetMapping
    public String getBeverages(Model model) {
        List<Crate> crates = crateRepository.findByOrderByNameAsc();

        model.addAttribute("crates", crates);

        return "crates";
    }
}

package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.model.Crate;
import de.uniba.dsg.beverage_store.service.BeverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/crate")
public class CrateController {

    private final BeverageService beverageService;

    @Autowired
    public CrateController(BeverageService beverageService) {
        this.beverageService = beverageService;
    }

    @GetMapping
    public String getBeverages(Model model) {
        List<Crate> crates = beverageService.getAllCrates();

        model.addAttribute("crates", crates);

        return "crates";
    }
}

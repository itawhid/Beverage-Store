package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.model.Bottle;
import de.uniba.dsg.beverage_store.service.BeverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/bottle")
public class BottleController {

    private final BeverageService beverageService;

    @Autowired
    public BottleController(BeverageService beverageService) {
        this.beverageService = beverageService;
    }

    @GetMapping
    public String getBeverages(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<Bottle> bottlePage = beverageService.getPagedBottles(page);

        model.addAttribute("bottles", bottlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("numberOfPages", bottlePage.getTotalPages());

        return "bottles";
    }
}

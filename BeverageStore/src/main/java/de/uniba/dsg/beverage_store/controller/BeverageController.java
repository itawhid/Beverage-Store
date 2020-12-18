package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.model.DropdownListItem;
import de.uniba.dsg.beverage_store.model.db.Bottle;
import de.uniba.dsg.beverage_store.model.db.Crate;
import de.uniba.dsg.beverage_store.model.dto.BottleDTO;
import de.uniba.dsg.beverage_store.model.dto.CrateDTO;
import de.uniba.dsg.beverage_store.service.BeverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/beverage")
public class BeverageController {

    private final BeverageService beverageService;

    @Autowired
    public BeverageController(BeverageService beverageService) {
        this.beverageService = beverageService;
    }

    @GetMapping(value = "/bottle")
    public String getBottles(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<Bottle> bottlePage = beverageService.getPagedBottlesWithAllowedStock(page);

        model.addAttribute("bottles", bottlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("numberOfPages", bottlePage.getTotalPages());

        return "beverage/bottle/list";
    }

    @GetMapping(value = "/bottle/add")
    public String getAddBottle(Model model) {
        model.addAttribute("bottleDTO", new BottleDTO());

        return "beverage/bottle/add";
    }

    @PostMapping(value = "/bottle/add")
    public String addBottle(@Valid BottleDTO bottleDTO, Errors errors, Model model) {
        boolean hasModelError = false, hasServerError = false;

        if (errors.hasErrors()) {
            hasModelError = true;
        }

        if (!hasModelError) {
            try {
                beverageService.addBottle(bottleDTO);
            } catch (Exception e) {
                hasServerError = true;
            }
        }

        if (hasModelError || hasServerError) {
            model.addAttribute("hasServerError", hasServerError);

            return "beverage/bottle/add";
        }

        return "redirect:/beverage/bottle";
    }

    @GetMapping(value = "/crate")
    public String getCrates(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<Crate> cratePage = beverageService.getPagedCratesAllowedStock(page);

        model.addAttribute("crates", cratePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("numberOfPages", cratePage.getTotalPages());

        return "beverage/crate/list";
    }

    @GetMapping(value = "/crate/add")
    public String getAddCrate(Model model) {
        model.addAttribute("crateDTO", new CrateDTO());
        model.addAttribute("bottleDropdownListItems", getBottleDropdownList());

        return "beverage/crate/add";
    }

    @PostMapping(value = "/crate/add")
    public String addCrate(@Valid CrateDTO crateDTO, Errors errors, Model model) {
        boolean hasModelError = false, hasServerError = false;

        if (errors.hasErrors()) {
            hasModelError = true;
        }

        if (!hasModelError) {
            try {
                beverageService.addCrate(crateDTO);
            } catch (Exception e) {
                hasServerError = true;
            }
        }

        if (hasModelError || hasServerError) {
            model.addAttribute("hasServerError", hasServerError);
            model.addAttribute("bottleDropdownListItems", getBottleDropdownList());

            return "beverage/crate/add";
        }

        return "redirect:/beverage/crate";
    }

    private List<DropdownListItem<Long>> getBottleDropdownList() {
        return beverageService.getBottles()
                .stream()
                .map(Bottle::getDropdownListItem)
                .collect(Collectors.toList());
    }
}

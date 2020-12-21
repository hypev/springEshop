package kz.app.hometask7.controllers;

import kz.app.hometask7.enitities.Users;
import kz.app.hometask7.services.ItemsService;
import kz.app.hometask7.services.SearchService;
import kz.app.hometask7.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/search")
public class SearchController {

    @Autowired
    ItemsService itemsService;

    @Autowired
    SearchService searchService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/")
    public String search(Model model,
                         @RequestParam(name = "q", required = false) String name,
                         @RequestParam(name = "s", required = false) String priceStart,
                         @RequestParam(name = "e", required = false) String priceEnd,
                         @RequestParam(name = "o", defaultValue = "0") boolean order,
                         @RequestParam(name = "b", required = false) Long brandId,
                         @RequestParam(name = "c", required = false) Long categoryId) {
        if (name != null)
            model.addAttribute("name", name);
        if (priceStart != null)
            model.addAttribute("start", priceStart);
        if (priceEnd != null)
            model.addAttribute("end", priceEnd);
        if (brandId != null)
            model.addAttribute("brand", brandId);
        if (categoryId != null)
            model.addAttribute("category", categoryId);
        model.addAttribute("user", getUserData());
        model.addAttribute("order", order);
        model.addAttribute("items", searchService.getFilteredItems(name, priceStart, priceEnd, order, brandId, categoryId));
        model.addAttribute("brands", itemsService.getAllBrands());
        model.addAttribute("categories", itemsService.getAllCategories());
        return "search";
    }

    private Users getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User)authentication.getPrincipal();
            return userService.getUserByEmail(secUser.getUsername());
        }
        return null;
    }

}

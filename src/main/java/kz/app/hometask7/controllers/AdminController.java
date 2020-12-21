package kz.app.hometask7.controllers;

import kz.app.hometask7.enitities.*;
import kz.app.hometask7.services.BasketService;
import kz.app.hometask7.services.ItemsService;
import kz.app.hometask7.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private UserService userService;

    @Autowired
    private BasketService basketService;

    @Value("${file.avatar.defaultAvatar}")
    private String avatarDefaultAvatar;

    @Value("${file.item.viewPath}")
    private String itemViewPath;

    @Value("${file.item.uploadPath}")
    private String itemUploadPath;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping(value = "")
    public String admin(Model model) {
        model.addAttribute("user", getUserData());
        return "redirect:/admin/items";
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping(value = "/items")
    public String items(Model model) {
        model.addAttribute("user", getUserData());
        model.addAttribute("items", itemsService.getAllItems());
        model.addAttribute("brands", itemsService.getAllBrands());
        return "admin_items";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping(value = "/item/{id}")
    public String item(Model model,
                       @PathVariable(name = "id") Long id) {
        Items item = itemsService.getItem(id);
        if (item != null) {
            model.addAttribute("user", getUserData());
            model.addAttribute("item", item);
            model.addAttribute("brands", itemsService.getAllBrands());
            model.addAttribute("categories", itemsService.getAllCategories());
            model.addAttribute("pictures", itemsService.getAllPicturesByItem(item.getId()));
            return "admin_item";
        }
        return "redirect:/admin/items";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping(value = "/add_item")
    public String addItem(@RequestParam(name = "name", defaultValue = "No Name") String name,
                          @RequestParam(name = "description", defaultValue = "") String description,
                          @RequestParam(name = "price", defaultValue = "0") double price,
                          @RequestParam(name = "stars", defaultValue = "0") int stars,
                          @RequestParam(name = "brand", defaultValue = "0") Long brandId,
                          @RequestParam(name = "smallPicUrl", defaultValue = "https://www.generationsforpeace.org/wp-content/uploads/2018/03/empty-300x240.jpg") String smallPicUrl,
                          @RequestParam(name = "largePicUrl", defaultValue = "https://www.generationsforpeace.org/wp-content/uploads/2018/03/empty-300x240.jpg") String largePicUrl,
                          @RequestParam(name = "inTopPage", defaultValue = "0") boolean inTopPage) {
        Brands brand = itemsService.getBrand(brandId);
        if (brand != null) {
            Items item = itemsService.addItems(new Items(
                    null,
                    name,
                    description,
                    price,
                    stars,
                    smallPicUrl,
                    largePicUrl,
                    new Timestamp(System.currentTimeMillis()),
                    inTopPage,
                    brand,
                    null
            ));
            return "redirect:/admin/item/" + item.getId();
        }
        return "redirect:/admin/";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping(value = "/edit_item")
    public String saveItem(@RequestParam(name = "id", defaultValue = "0") Long id,
                           @RequestParam(name = "name", defaultValue = "No Name") String name,
                           @RequestParam(name = "description", defaultValue = "") String description,
                           @RequestParam(name = "price", defaultValue = "0") double price,
                           @RequestParam(name = "stars", defaultValue = "0") int stars,
                           @RequestParam(name = "brand", defaultValue = "0") Long brandId,
                           @RequestParam(name = "smallPicUrl", defaultValue = "https://www.generationsforpeace.org/wp-content/uploads/2018/03/empty-300x240.jpg") String smallPicUrl,
                           @RequestParam(name = "largePicUrl", defaultValue = "https://www.generationsforpeace.org/wp-content/uploads/2018/03/empty-300x240.jpg") String largePicUrl,
                           @RequestParam(name = "inTopPage", defaultValue = "0") boolean inTopPage) {
        Brands brand = itemsService.getBrand(brandId);
        if (brand != null) {
            Items item = itemsService.saveItem(new Items(
                    id,
                    name,
                    description,
                    price,
                    stars,
                    smallPicUrl,
                    largePicUrl,
                    null,
                    inTopPage,
                    brand,
                    null
            ));
            if (item != null) {
                return "redirect:/admin/item/" + id;
            }
        }
        return "redirect:/admin/items";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping(value = "/delete_item")
    public String deleteItem(@RequestParam(name = "id") Long id) {
        itemsService.deleteItem(id);
        return "redirect:/admin/items";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping(value = "/add_item_category")
    public String addCategory(@RequestParam(name = "item_id") Long item_id,
                              @RequestParam(name = "category_id") Long category_id) {
        Items item = itemsService.getItem(item_id);
        List<Categories> categories = item.getCategories();
        if (categories == null) {
            categories = new ArrayList<>();
        }
        Categories category = itemsService.getCategory(category_id);
        if (category != null) {
            int index = item.getCategories().indexOf(category);
            if (index == -1)
                categories.add(category);
            else
                categories.remove(index);

            item = itemsService.saveItem(item);
            if (item != null) {
                return "redirect:/admin/item/" + item_id + "#categories";
            }
        }
        return "redirect:/admin/items";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping(value = "/add_item_picture")
    public String addPicture(@RequestParam(name = "item_id") Long item_id,
                             @RequestParam(name = "picture")MultipartFile file) {
        if (Objects.equals(file.getContentType(), "image/jpeg") || Objects.equals(file.getContentType(), "image/png")) {
            try {
                Items item = itemsService.getItem(item_id);
                String fileName = DigestUtils.sha1Hex("picture_" + item.getId() + System.currentTimeMillis() + "_!Picture");
                byte[] bytes = file.getBytes();
                Path path = Paths.get(itemUploadPath + fileName + ".jpg");
                Files.write(path, bytes);
                itemsService.addPicture(new Pictures(
                        null,
                        fileName,
                        new Timestamp(System.currentTimeMillis()),
                        item
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/admin/item/" + item_id + "#pictures";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping(value = "/delete_item_picture")
    public String deletePicture(@RequestParam(name = "item_id")Long item_id,
                                @RequestParam(name = "picture_id")Long picture_id) {
        itemsService.deletePicture(picture_id);
        return "redirect:/admin/item/" + item_id;
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/categories")
    public String categories(Model model) {
        model.addAttribute("user", getUserData());
        model.addAttribute("categories", itemsService.getAllCategories());
        return "admin_categories";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/add_category")
    public String addCategories(@RequestParam(name = "name", defaultValue = "Default Name") String name,
                                @RequestParam(name = "icon", defaultValue = "fab fa-atlassian") String icon) {

        itemsService.addCategory(new Categories(
                null,
                name,
                icon
        ));

        return "redirect:/admin/categories";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/edit_category")
    public String editCategory(@RequestParam(name = "id", defaultValue = "0") Long id,
                               @RequestParam(name = "name", defaultValue = "Default Name") String name,
                               @RequestParam(name = "icon", defaultValue = "fab fa-atlassian") String icon) {

        itemsService.saveCategory(new Categories(
                id,
                name,
                icon
        ));

        return "redirect:/admin/categories";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/delete_category")
    public String deleteCategory(@RequestParam(name = "id", defaultValue = "0") Long id) {

        itemsService.deleteCategory(id);

        return "redirect:/admin/categories";
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/brands")
    public String brands(Model model) {
        model.addAttribute("user", getUserData());
        model.addAttribute("countries", itemsService.getAllCountries());
        model.addAttribute("brands", itemsService.getAllBrands());
        return "admin_brands";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/add_brand")
    public String addBrand(@RequestParam(name = "name", defaultValue = "Default Name") String name,
                           @RequestParam(name = "countryId", defaultValue = "0") Long id) {

        Countries country = itemsService.getCountry(id);
        if (country != null) {
            itemsService.addBrand(new Brands(
                    null,
                    name,
                    country
            ));
        }

        return "redirect:/admin/brands";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/delete_brand")
    public String deleteBrand(@RequestParam(name = "id", defaultValue = "0") Long id) {

        itemsService.deleteBrand(id);

        return "redirect:/admin/brands";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/edit_brand")
    public String editBrand(@RequestParam(name = "id", defaultValue = "0") Long id,
                            @RequestParam(name = "name", defaultValue = "Default Name") String name,
                            @RequestParam(name = "countryId", defaultValue = "0") Long countryId) {

        Countries country = itemsService.getCountry(countryId);
        if (country != null)
            itemsService.saveBrand(new Brands(
                    id,
                    name,
                    country
            ));

        return "redirect:/admin/brands";
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/countries")
    public String countries(Model model) {
        model.addAttribute("user", getUserData());
        model.addAttribute("countries", itemsService.getAllCountries());
        return "admin_countries";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/add_country")
    public String addCountry(@RequestParam(name = "name", defaultValue = "Default Name") String name,
                             @RequestParam(name = "code", defaultValue = "Default Code") String code) {

        itemsService.addCountry(new Countries(
                null,
                name,
                code
        ));

        return "redirect:/admin/countries";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/delete_country")
    public String deleteCountry(@RequestParam(name = "id", defaultValue = "0") Long id) {

        itemsService.deleteCountry(id);

        return "redirect:/admin/countries";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/edit_country")
    public String editCountry(@RequestParam(name = "id", defaultValue = "0") Long id,
                              @RequestParam(name = "name", defaultValue = "Default Name") String name,
                              @RequestParam(name = "code", defaultValue = "Default Code") String code) {

        itemsService.saveCountry(new Countries(
                id,
                name,
                code
        ));

        return "redirect:/admin/countries";
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/users")
    public String users(Model model) {
        model.addAttribute("user", getUserData());
        model.addAttribute("users", userService.getAllUsers());
        return "admin_users";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/user/{id}")
    public String user(Model model,
                       @PathVariable(name = "id") Long id) {
        Users user = userService.getUser(id);
        if (user != null) {
            model.addAttribute("user", getUserData());
            model.addAttribute("user", user);
            model.addAttribute("roles", userService.getAllRoles());
            return "admin_user";
        }
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/add_user")
    public String addUser(@RequestParam(name = "fullname", defaultValue = "No Name") String fullname,
                          @RequestParam(name = "email", defaultValue = "#") String email,
                          @RequestParam(name = "password", defaultValue = "aaa")String password) {
        Users user = userService.addUser(new Users(
                null,
                email,
                password,
                fullname,
                avatarDefaultAvatar,
                null
        ));
        if (user != null)
            return "redirect:/admin/user/" + user.getId();
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/edit_user")
    public String saveUser(@RequestParam(name = "id", defaultValue = "0") Long id,
                           @RequestParam(name = "fullname", defaultValue = "No Name") String fullname) {
            Users user = userService.saveUser(new Users(
                    id,
                    null,
                    null,
                    fullname,
                    avatarDefaultAvatar,
                    null
            ));
            if (user != null) {
                return "redirect:/admin/user/" + id;
            }
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/delete_user")
    public String deleteUser(@RequestParam(name = "id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/add_user_role")
    public String addRoleToUser(@RequestParam(name = "user_id") Long user_id,
                              @RequestParam(name = "role_id") Long role_id) {
        Users user = userService.getUser(user_id);
        List<Roles> roles = user.getRoles();
        if (roles == null) {
            roles = new ArrayList<>();
        }
        Roles role = userService.getRole(role_id);
        if (role != null) {
            int index = user.getRoles().indexOf(role);
            if (index == -1)
                roles.add(role);
            else
                roles.remove(role);

            user = userService.saveUser(user);
            if (user != null) {
                return "redirect:/admin/user/" + user_id;
            }
        }
        return "redirect:/admin/users";
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/roles")
    public String roles(Model model) {
        model.addAttribute("user", getUserData());
        model.addAttribute("roles", userService.getAllRoles());
        return "admin_roles";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/add_role")
    public String addRole(@RequestParam(name = "name", defaultValue = "Default Name") String name,
                             @RequestParam(name = "description", defaultValue = "Default Description") String description) {

        userService.addRole(new Roles(
                null,
                name,
                description
        ));

        return "redirect:/admin/roles";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/delete_role")
    public String deleteRole(@RequestParam(name = "id", defaultValue = "0") Long id) {

        userService.deleteRole(id);

        return "redirect:/admin/roles";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/edit_role")
    public String editRole(@RequestParam(name = "id", defaultValue = "0") Long id,
                              @RequestParam(name = "name", defaultValue = "Default Name") String name,
                              @RequestParam(name = "description", defaultValue = "Default Description") String description) {

        userService.saveRole(new Roles(
                id,
                name,
                description
        ));

        return "redirect:/admin/roles";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/orders")
    public String orders(Model model) {
        model.addAttribute("user", getUserData());
        model.addAttribute("roles", userService.getAllRoles());
        model.addAttribute("orders", basketService.getAllOrders());
        return "admin_orders";
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

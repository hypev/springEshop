package kz.app.hometask7.controllers;

import kz.app.hometask7.enitities.*;
import kz.app.hometask7.services.BasketService;
import kz.app.hometask7.services.ItemsService;
import kz.app.hometask7.services.UserService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.print.attribute.standard.Media;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

@Controller
public class MainController {

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private UserService userService;

    @Autowired
    private BasketService basketService;

    @Value("${file.avatar.viewPath}")
    private String avatarViewPath;

    @Value("${file.avatar.uploadPath}")
    private String avatarUploadPath;

    @Value("${file.avatar.defaultAvatar}")
    private String avatarDefaultAvatar;

    @Value("${file.item.viewPath}")
    private String itemViewPath;

    @Value("${file.item.uploadPath}")
    private String itemUploadPath;

    @GetMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("user", getUserData());
        model.addAttribute("items", itemsService.getAllItemsInTopPage());
        model.addAttribute("brands", itemsService.getAllBrands());
        model.addAttribute("categories", itemsService.getAllCategories());
        return "index";
    }

    @GetMapping(value = "/all")
    public String all(Model model) {
        model.addAttribute("user", getUserData());
        model.addAttribute("items", itemsService.getAllItems());
        model.addAttribute("brands", itemsService.getAllBrands());
        model.addAttribute("categories", itemsService.getAllCategories());
        return "all";
    }

    @GetMapping(value = "/items/{id}")
    public String details (Model model,
                           @PathVariable(name = "id") Long id,
                           @CookieValue(name = "basket", required = false) Long basket_id) {
        Items item = itemsService.getItem(id);
        if (item != null) {
            model.addAttribute("user", getUserData());
            model.addAttribute("item", item);
            model.addAttribute("brands", itemsService.getAllBrands());
            model.addAttribute("categories", itemsService.getAllCategories());
            model.addAttribute("pictures", itemsService.getAllPicturesByItem(item.getId()));
            model.addAttribute("comments", itemsService.getAllCommentsByItem(item.getId()));
            if (basket_id != null) {
                Baskets basket = basketService.getBasket(basket_id);
                model.addAttribute("basket", basket);
            }
            return "details";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/basket")
    public String basket (Model model,
                          @CookieValue(name = "basket", required = false) Long basket_id) {
        model.addAttribute("user", getUserData());
        model.addAttribute("brands", itemsService.getAllBrands());
        model.addAttribute("categories", itemsService.getAllCategories());
        Baskets basket = basketService.getBasket(basket_id);
        double totalPrice = 0;
        for (Orders o : basket.getOrders()) {
            totalPrice += o.getAmount() * o.getItem().getPrice();
        }
        model.addAttribute("basket", basket);
        model.addAttribute("total", totalPrice);
        return "basket";
    }

    @PostMapping(value = "/basket_add")
    public String addToBasket(@RequestParam(name = "item_id") Long item_id,
                              @CookieValue(name = "basket", required = false) Long basket_id,
                              HttpServletResponse response) {
        Items item = itemsService.getItem(item_id);
        if (item != null) {
            Baskets basket = null;
            if (basket_id == null) {
                basket = basketService.addBasket(new Baskets(null, new ArrayList<>()));
                Cookie cookie = new Cookie("basket", basket.getId().toString());
                cookie.setMaxAge(3600 * 24 * 60);
                response.addCookie(cookie);
            } else
                basket = basketService.getBasket(basket_id);
            if (basket.getOrders() == null) {
                basket.setOrders(new ArrayList<>());
            }
            Orders order = null;
            for (Orders o : basket.getOrders()) {
                if (o.getItem().getId().equals(item_id)) {
                    order = o;
                    break;
                }
            }
            if (order == null) {
                order = basketService.addOrder(new Orders(
                            null,
                            1,
                            item
                        ));
                basket.getOrders().add(order);
            } else {
                order.setAmount(order.getAmount() + 1);
            }
            basketService.saveOrder(order);
            basketService.saveBasket(basket);
        }

        return "redirect:/basket";
    }

    @PostMapping(value = "/basket_remove")
    public String removeFromBasket(@RequestParam(name = "item_id") Long item_id,
                                   @CookieValue(name = "basket", required = false) Long basket_id,
                                   HttpServletResponse response) {
        Items item = itemsService.getItem(item_id);
        if (item != null) {
            Baskets basket = null;
            if (basket_id == null) {
                basket = basketService.addBasket(new Baskets(null, new ArrayList<>()));
                Cookie cookie = new Cookie("basket", basket.getId().toString());
                cookie.setMaxAge(3600 * 24 * 60);
                response.addCookie(cookie);
            } else
                basket = basketService.getBasket(basket_id);
            if (basket.getOrders() == null) {
                basket.setOrders(new ArrayList<>());
            }
            Orders order = null;
            for (Orders o : basket.getOrders()) {
                if (o.getItem().getId().equals(item_id)) {
                    order = o;
                    break;
                }
            }
            if (order != null) {
                order.setAmount(order.getAmount() - 1);
                if (order.getAmount() == 0) {
                    basket.getOrders().remove(order);
                    basketService.deleteOrder(order.getId());
                }
                else
                    basketService.saveOrder(order);
            }
            basketService.saveBasket(basket);
        }
        return "redirect:/basket";
    }

    @PostMapping(value = "/basket_clear")
    public String clearBasket(@CookieValue(name = "basket", required = false) Long basket_id) {
        Baskets basket = null;
        if (basket_id != null) {
            basket = basketService.getBasket(basket_id);
            if (basket.getOrders() != null) {
                basket.getOrders().clear();
                basketService.saveBasket(basket);
            }
        }
        return "redirect:/basket";
    }

    @PostMapping(value = "/check_in")
    public String check_in(@CookieValue(name = "basket", required = false) Long basket_id,
                           @RequestParam(name = "fullname") String fullname,
                           @RequestParam(name = "card") String card,
                           @RequestParam(name = "expire") String expiration,
                           @RequestParam(name = "cvv") String cvv) {
        Baskets basket = basketService.getBasket(basket_id);
        if (basket != null) {
            if (basket.getOrders() != null && basket.getOrders().size() != 0) {
                Checkins checkin = new Checkins(
                        null,
                        fullname,
                        new ArrayList<>()
                );
                for (Orders o : basket.getOrders()) {
                    checkin.getOrders().add(o);
                }
                basketService.addCheckins(checkin);
                basket.getOrders().clear();
                basketService.saveBasket(basket);
                return "redirect:/check_in/success";
            }
        }
        return "redirect:/basket";
    }

    @GetMapping(value = "/check_in/success")
    public String checkinSuccess() {
        return "checkinSuccess";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/add_comment")
    public String addComment(@RequestParam(name = "item_id")Long item_id,
                             @RequestParam(name = "comment")String comment) {
        Users user = getUserData();
        Items item = itemsService.getItem(item_id);
        if (item != null) {
            itemsService.addComment(new Comments(
                    null,
                    comment,
                    new Timestamp(System.currentTimeMillis()),
                    item,
                    user
            ));
        }
        return "redirect:/items/" + item_id + "#comments";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/delete_comment")
    public String deleteComment(@RequestParam(name = "item_id")Long item_id,
                                @RequestParam(name = "comment_id")Long comment_id) {
        Users user = getUserData();
        Comments comment = itemsService.getComment(comment_id);
        if (comment != null) {
            if (comment.getAuthor().getId().equals(user.getId()) ||
                    user.stringRoles().contains("ROLE_ADMIN") ||
                    user.stringRoles().contains("ROLE_MODERATOR")) {
                if (comment.getItem().getId().equals(item_id)) {
                    itemsService.deleteComment(comment_id);
                }
            }
        }
        return "redirect:/items/" + item_id + "#comments";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/edit_comment")
    public String editComment(@RequestParam(name = "item_id")Long item_id,
                              @RequestParam(name = "comment_id")Long comment_id,
                              @RequestParam(name = "comment")String comment_str) {
        Users user = getUserData();
        Comments comment = itemsService.getComment(comment_id);
        if (comment != null) {
            if (comment.getAuthor().getId().equals(user.getId()) ||
                    user.stringRoles().contains("ROLE_ADMIN") ||
                    user.stringRoles().contains("ROLE_MODERATOR")) {
                if (comment.getItem().getId().equals(item_id)) {
                    itemsService.saveComment(new Comments(
                            comment_id,
                            comment_str,
                            null,
                            itemsService.getItem(item_id),
                            user
                    ));
                }
            }
        }
        return "redirect:/items/" + item_id + "#comments";
    }

    @GetMapping(value = "/view/pictures/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewUserPicture(@PathVariable(name = "url")String url) throws IOException {
        String pictureUrl = avatarViewPath + avatarDefaultAvatar;
        if (url != null || url.equals("null")) {
            pictureUrl = avatarViewPath + url + ".jpg";
        }
        InputStream in;
        try {
            ClassPathResource resource = new ClassPathResource(pictureUrl);
            in = resource.getInputStream();
        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(avatarViewPath + avatarDefaultAvatar);
            in = resource.getInputStream();
        }

        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/view/item/pictures/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewItemPicture(@PathVariable(name = "url")String url) throws IOException {
        String pictureUrl = itemViewPath + url + ".jpg";
        InputStream in;
        try {
            ClassPathResource resource = new ClassPathResource(pictureUrl);
            in = resource.getInputStream();
        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(pictureUrl);
            in = resource.getInputStream();
        }

        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/403")
    public String accessDenied(Model model) {
        model.addAttribute("user", getUserData());
        model.addAttribute("items", itemsService.getAllItems());
        model.addAttribute("brands", itemsService.getAllBrands());
        model.addAttribute("categories", itemsService.getAllCategories());
        return "403";
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

package kz.app.hometask7.controllers;

import kz.app.hometask7.enitities.Users;
import kz.app.hometask7.services.ItemsService;
import kz.app.hometask7.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Controller
public class UserController {

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private UserService userService;

    @Value("${file.avatar.viewPath}")
    private String avatarViewPath;

    @Value("${file.avatar.uploadPath}")
    private String avatarUploadPath;

    @Value("${file.avatar.defaultAvatar}")
    private String avatarDefaultAvatar;

    @GetMapping(value = "/login")
    @PreAuthorize("isAnonymous()")
    public String login(Model model,
                        @RequestParam(name = "error", required = false)String error,
                        @RequestParam(name = "success", required = false)String success) {

        model.addAttribute("error", error != null);
        model.addAttribute("success", success);
        model.addAttribute("user", getUserData());
        model.addAttribute("items", itemsService.getAllItems());
        model.addAttribute("brands", itemsService.getAllBrands());
        model.addAttribute("categories", itemsService.getAllCategories());
        return "login";
    }

    @GetMapping(value = "/register")
    @PreAuthorize("isAnonymous()")
    public String register(Model model,
                           @RequestParam(name = "error", required = false)String error) {
        model.addAttribute("error", error);
        model.addAttribute("user", getUserData());
        model.addAttribute("items", itemsService.getAllItems());
        model.addAttribute("brands", itemsService.getAllBrands());
        model.addAttribute("categories", itemsService.getAllCategories());
        return "registration";
    }

    @PostMapping(value = "/register")
    @PreAuthorize("isAnonymous()")
    public String registerPost(@RequestParam(name = "email") String email,
                               @RequestParam(name = "password") String password,
                               @RequestParam(name = "rePassword") String rePassword,
                               @RequestParam(name = "fullname") String fullname) {
        if (password.equals(rePassword)) {
            Users user = new Users();
            user.setPassword(password);
            user.setEmail(email);
            user.setFullname(fullname);
            if (userService.createUser(user) != null) {
                return "redirect:/login?success=Register";
            }
            return "redirect:/register?error=Exist";
        }
        return "redirect:/register?error=Retype";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model,
                          @RequestParam(name = "error", required = false)String error,
                          @RequestParam(name = "success", required = false)String success) {
        model.addAttribute("error", error);
        model.addAttribute("success", success);
        model.addAttribute("user", getUserData());
        model.addAttribute("items", itemsService.getAllItems());
        model.addAttribute("brands", itemsService.getAllBrands());
        model.addAttribute("categories", itemsService.getAllCategories());
        return "profile";
    }

    @PostMapping(value = "/profile/update")
    @PreAuthorize("isAuthenticated()")
    public String profileUpdate(@RequestParam(name = "fullname") String fullname) {
        Users user = getUserData();
        if (user != null) {
            user.setFullname(fullname);
            if (userService.updateUserFullname(user) != null) {
                return "redirect:/profile?success=Profile";
            }
        }
        return "redirect:/profile?error=Profile";
    }

    @PostMapping(value = "/profile/update-password")
    @PreAuthorize("isAuthenticated()")
    public String profilePasswordUpdate(@RequestParam(name = "oldPassword") String oldPassword,
                                        @RequestParam(name = "password") String password,
                                        @RequestParam(name = "rePassword") String rePassword) {
        if (password.equals(rePassword)) {
            Users user = getUserData();
            if (userService.updateUserPassword(user, oldPassword, password) != null) {
                return "redirect:/profile?success=Password";
            }
            return "redirect:/profile?error=OldPassword";
        }
        return "redirect:/profile?error=Retype";
    }

    @PostMapping(value = "/profile/update-avatar")
    @PreAuthorize("isAuthenticated()")
    public String profileAvatarUpdate(@RequestParam(name = "avatar") MultipartFile file) {
        if (Objects.equals(file.getContentType(), "image/jpeg") || Objects.equals(file.getContentType(), "image/png")) {
            try {
                Users user = getUserData();
                String fileName = DigestUtils.sha1Hex("avatar_"+user.getId()+"_!Picture");
                byte[] bytes = file.getBytes();
                Path path = Paths.get(avatarUploadPath + fileName + ".jpg");
                Files.write(path, bytes);
                user.setPictureUrl(fileName);
                userService.saveUser(user);
                return "redirect:/profile?success=Avatar";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/profile?error=Avatar";
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

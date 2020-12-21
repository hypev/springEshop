package kz.app.hometask7.config;

import kz.app.hometask7.services.BasketService;
import kz.app.hometask7.services.ItemsService;
import kz.app.hometask7.services.SearchService;
import kz.app.hometask7.services.UserService;
import kz.app.hometask7.services.implementation.BasketServiceImplementation;
import kz.app.hometask7.services.implementation.ItemsServiceImplementation;
import kz.app.hometask7.services.implementation.SearchServiceImplementation;
import kz.app.hometask7.services.implementation.UserServiceImplementation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean
    public ItemsService itemsService() {
        return new ItemsServiceImplementation();
    }

    @Bean
    public SearchService searchService() {
        return new SearchServiceImplementation();
    }

    @Bean
    public BasketService basketService() { return new BasketServiceImplementation(); }

    @Bean
    public UserService userService() { return new UserServiceImplementation(); }

}

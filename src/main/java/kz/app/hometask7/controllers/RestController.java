package kz.app.hometask7.controllers;

import kz.app.hometask7.enitities.Baskets;
import kz.app.hometask7.enitities.Orders;
import kz.app.hometask7.services.BasketService;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private BasketService basketService;

    @RequestMapping(value = "/basket/amount", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map getBasketAmount(@CookieValue(name = "basket", required = false) Long basket_id) {
        Baskets basket = basketService.getBasket(basket_id);
        int amount = 0;
        for (Orders o : basket.getOrders()) {
            amount += o.getAmount();
        }
        return Collections.singletonMap("response", amount);
    }

}

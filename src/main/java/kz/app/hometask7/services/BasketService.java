package kz.app.hometask7.services;

import kz.app.hometask7.enitities.Baskets;
import kz.app.hometask7.enitities.Checkins;
import kz.app.hometask7.enitities.Orders;

import java.util.List;

public interface BasketService {

    Baskets addBasket(Baskets basket);
    Baskets getBasket(Long id);
    void deleteBasket(Long id);
    Baskets saveBasket(Baskets basket);

    Orders addOrder(Orders order);
    Orders getOrder(Long id);
    void deleteOrder(Long id);
    void saveOrder(Orders order);

    List<Checkins> getAllOrders();
    void addCheckins(Checkins checkin);
    Checkins getCheckins(Long id);
    void deleteCheckins(Long id);
    Checkins saveCheckins(Checkins checkin);

}

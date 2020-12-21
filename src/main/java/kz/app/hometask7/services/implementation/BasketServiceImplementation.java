package kz.app.hometask7.services.implementation;

import kz.app.hometask7.enitities.Baskets;
import kz.app.hometask7.enitities.Checkins;
import kz.app.hometask7.enitities.Orders;
import kz.app.hometask7.repositories.BasketsRepository;
import kz.app.hometask7.repositories.CheckinsRepository;
import kz.app.hometask7.repositories.OrdersRepository;
import kz.app.hometask7.services.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasketServiceImplementation implements BasketService {

    @Autowired
    private BasketsRepository basketsRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private CheckinsRepository checkinsRepository;

    @Override
    public Baskets addBasket(Baskets basket) {
        return basketsRepository.save(basket);
    }

    @Override
    public Baskets getBasket(Long id) {
        return basketsRepository.getOne(id);
    }

    @Override
    public void deleteBasket(Long id) {
        Baskets basket = basketsRepository.getOne(id);
        if (basket != null) {
            basketsRepository.delete(basket);
        }
    }

    @Override
    public Baskets saveBasket(Baskets basket) {
        Baskets saved = basketsRepository.getOne(basket.getId());
        if (saved != null) {
            saved.setOrders(basket.getOrders());
            return basketsRepository.save(saved);
        }
        return basket;
    }

    @Override
    public Orders addOrder(Orders order) {
        return ordersRepository.save(order);
    }

    @Override
    public Orders getOrder(Long id) {
        return ordersRepository.getOne(id);
    }

    @Override
    public void deleteOrder(Long id) {
        Orders order = ordersRepository.getOne(id);
        if (order != null) {
            ordersRepository.delete(order);
        }
    }

    @Override
    public void saveOrder(Orders order) {
        Orders saved = ordersRepository.getOne(order.getId());
        if (saved != null) {
            saved.setAmount(order.getAmount());
            ordersRepository.save(saved);
        }
    }

    @Override
    public List<Checkins> getAllOrders() {
        return checkinsRepository.findAll();
    }

    @Override
    public void addCheckins(Checkins checkin) {
        checkinsRepository.save(checkin);
    }

    @Override
    public Checkins getCheckins(Long id) {
        return checkinsRepository.getOne(id);
    }

    @Override
    public void deleteCheckins(Long id) {
        Checkins checkin = checkinsRepository.getOne(id);
        if (checkin != null) {
            checkinsRepository.delete(checkin);
        }
    }

    @Override
    public Checkins saveCheckins(Checkins checkin) {
        Checkins saved = checkinsRepository.getOne(checkin.getId());
        if (saved != null) {
            saved.setFullname(checkin.getFullname());
            saved.setOrders(checkin.getOrders());
            return checkinsRepository.save(saved);
        }
        return checkin;
    }
}

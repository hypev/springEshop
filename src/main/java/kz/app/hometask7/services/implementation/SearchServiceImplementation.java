package kz.app.hometask7.services.implementation;

import kz.app.hometask7.enitities.Brands;
import kz.app.hometask7.enitities.Categories;
import kz.app.hometask7.enitities.Items;
import kz.app.hometask7.repositories.CategoriesRepository;
import kz.app.hometask7.repositories.ItemsRepository;
import kz.app.hometask7.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImplementation implements SearchService {

    @Autowired
    private ItemsRepository itemsRepository;

    @Override
    public List<Items> getFilteredItems(String name, String priceStart, String priceEnd, boolean order, Long brandId, Long categoryId) {
        if (name != null)
            name = "%" + name.toLowerCase() + "%";
        else
            name = "%%";
        double start = (priceStart != null && !priceStart.equals("")) ? Double.parseDouble(priceStart) : 0;
        double end = (priceEnd != null && !priceEnd.equals("")) ? Double.parseDouble(priceEnd) : 1000000;

        List<Items> items = new ArrayList<>();

        if (order)
            items = itemsRepository.findAllByNameLikeAndPriceBetweenOrderByPriceDesc(name, start, end);
        else
            items = itemsRepository.findAllByNameLikeAndPriceBetweenOrderByPriceAsc(name, start, end);

        if (brandId != null)
            items.removeIf(i -> !i.getBrand().getId().equals(brandId));
        if (categoryId != null) {
            List<Items> filteredItems = new ArrayList<>();
            for (Items i : items) {
                boolean f = false;
                for (Categories c : i.getCategories()) {
                    if (categoryId.equals(c.getId())) {
                        f = true;
                        break;
                    }
                }
                if (f)
                    filteredItems.add(i);
            }
            return filteredItems;
        }
        return items;

    }
}

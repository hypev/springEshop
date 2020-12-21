package kz.app.hometask7.services;

import kz.app.hometask7.enitities.Brands;
import kz.app.hometask7.enitities.Items;

import java.util.List;

public interface SearchService {

    List<Items> getFilteredItems(String name, String priceStart, String priceEnd, boolean order, Long brandId, Long categoryId);

}

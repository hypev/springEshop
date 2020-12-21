package kz.app.hometask7.services;

import kz.app.hometask7.enitities.*;

import java.util.List;

public interface ItemsService {

    Items addItems(Items item);
    List<Items> getAllItems();
    List<Items> getAllItemsInTopPage();
    Items getItem(Long id);
    void deleteItem(Long id);
    Items saveItem(Items item);

    void addCountry(Countries country);
    List<Countries> getAllCountries();
    Countries getCountry(Long id);
    void deleteCountry(Long id);
    void saveCountry(Countries country);

    void addBrand(Brands brand);
    List<Brands> getAllBrands();
    Brands getBrand(Long id);
    void deleteBrand(Long id);
    void saveBrand(Brands brand);

    void addCategory(Categories category);
    List<Categories> getAllCategories();
    Categories getCategory(Long id);
    void deleteCategory(Long id);
    void saveCategory(Categories category);

    void addPicture(Pictures picture);
    List<Pictures> getAllPicturesByItem(Long item_id);
    Pictures getPicture(Long id);
    void deletePicture(Long id);
    void savePicture(Pictures picture);

    void addComment(Comments comment);
    List<Comments> getAllCommentsByItem(Long item_id);
    Comments getComment(Long id);
    void deleteComment(Long id);
    void saveComment(Comments comment);

}

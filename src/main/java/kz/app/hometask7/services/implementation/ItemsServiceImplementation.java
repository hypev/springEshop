package kz.app.hometask7.services.implementation;

import kz.app.hometask7.enitities.*;
import kz.app.hometask7.repositories.*;
import kz.app.hometask7.services.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemsServiceImplementation implements ItemsService {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private CountriesRepository countriesRepository;

    @Autowired
    private BrandsRepository brandsRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private PicturesRepository picturesRepository;

    @Autowired
    private CommentsRepository commentsRepository;


//  [ ITEMS ]

    @Override
    public Items addItems(Items item) {
        return itemsRepository.save(item);
    }

    @Override
    public List<Items> getAllItems() {
        return itemsRepository.findAll();
    }

    @Override
    public List<Items> getAllItemsInTopPage() {
        return itemsRepository.findAllByInTopPageTrueOrderByAddedDateDesc();
    }

    @Override
    public Items getItem(Long id) {
        return itemsRepository.getOne(id);
    }

    @Override
    public void deleteItem(Long id) {
        Items item = getItem(id);
        if (item != null) {
            List<Pictures> pictures = picturesRepository.findAllByItemEquals(item);
            for (Pictures p : pictures)
                deletePicture(p.getId());
            List<Comments> comments = commentsRepository.findAllByItemEqualsOrderByAddedDateDesc(item);
            for (Comments c : comments)
                deleteComment(c.getId());
            itemsRepository.delete(item);
        }
    }

    @Override
    public Items saveItem(Items item) {
        Items saved = getItem(item.getId());
        if (saved != null) {
            saved.setName(item.getName());
            saved.setDescription(item.getDescription());
            saved.setSmallPicUrl(item.getSmallPicUrl());
            saved.setLargePicUrl(item.getLargePicUrl());
            saved.setPrice(item.getPrice());
            saved.setStars(item.getStars());
            saved.setInTopPage(item.isInTopPage());
            saved.setBrand(item.getBrand());
            return itemsRepository.save(saved);
        }
        return null;
    }


//  [ COUNTRIES ]

    @Override
    public void addCountry(Countries country) {
        countriesRepository.save(country);
    }

    @Override
    public List<Countries> getAllCountries() {
        return countriesRepository.findAll();
    }

    @Override
    public Countries getCountry(Long id) {
        return countriesRepository.getOne(id);
    }

    @Override
    public void deleteCountry(Long id) {
        Countries country = getCountry(id);
        if (country != null) {
            countriesRepository.delete(country);
        }
    }

    @Override
    public void saveCountry(Countries country) {
        Countries saved = getCountry(country.getId());
        if (saved != null) {
            saved.setName(country.getName());
            saved.setCode(country.getCode());
            countriesRepository.save(saved);
        }
    }


//  [ BRANDS ]

    @Override
    public void addBrand(Brands brand) {
        brandsRepository.save(brand);
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandsRepository.findAll();
    }

    @Override
    public Brands getBrand(Long id) {
        return brandsRepository.getOne(id);
    }

    @Override
    public void deleteBrand(Long id) {
        Brands brand = getBrand(id);
        if (brand != null) {
            brandsRepository.delete(brand);
        }
    }

    @Override
    public void saveBrand(Brands brand) {
        Brands saved = getBrand(brand.getId());
        if (saved != null) {
            saved.setName(brand.getName());
            saved.setCountry(brand.getCountry());
            brandsRepository.save(saved);
        }
    }


//  [ CATEGORIES ]

    @Override
    public void addCategory(Categories category) {
        categoriesRepository.save(category);
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public Categories getCategory(Long id) {
        return categoriesRepository.getOne(id);
    }

    @Override
    public void deleteCategory(Long id) {
        Categories category = getCategory(id);
        if (category != null) {
            categoriesRepository.delete(category);
        }
    }

    @Override
    public void saveCategory(Categories category) {
        Categories saved = getCategory(category.getId());
        if (saved != null) {
            saved.setName(category.getName());
            categoriesRepository.save(saved);
        }
    }


//  [ PICTURES ]

    @Override
    public void addPicture(Pictures picture) {
        picturesRepository.save(picture);
    }

    @Override
    public List<Pictures> getAllPicturesByItem(Long item_id) {
        return picturesRepository.findAllByItemEquals(itemsRepository.getOne(item_id));
    }

    @Override
    public Pictures getPicture(Long id) {
        return picturesRepository.getOne(id);
    }

    @Override
    public void deletePicture(Long id) {
        Pictures picture = getPicture(id);
        if (picture != null) {
            picturesRepository.delete(picture);
        }
    }

    @Override
    public void savePicture(Pictures picture) {
        Pictures saved = getPicture(picture.getId());
        if (saved != null) {
            saved.setAddedDate(picture.getAddedDate());
            saved.setItem(picture.getItem());
            saved.setUrl(picture.getUrl());
            picturesRepository.save(saved);
        }
    }


//  [ COMMENTS ]

    @Override
    public void addComment(Comments comment) {
        commentsRepository.save(comment);
    }

    @Override
    public List<Comments> getAllCommentsByItem(Long item_id) {
        return commentsRepository.findAllByItemEqualsOrderByAddedDateDesc(itemsRepository.getOne(item_id));
    }

    @Override
    public Comments getComment(Long id) {
        return commentsRepository.getOne(id);
    }

    @Override
    public void deleteComment(Long id) {
        Comments comment = getComment(id);
        if (comment != null) {
            commentsRepository.delete(comment);
        }
    }

    @Override
    public void saveComment(Comments comment) {
        Comments saved = getComment(comment.getId());
        if (saved != null) {
            saved.setComment(comment.getComment());
            commentsRepository.save(saved);
        }
    }

}

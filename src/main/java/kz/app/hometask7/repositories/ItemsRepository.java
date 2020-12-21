package kz.app.hometask7.repositories;

import kz.app.hometask7.enitities.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ItemsRepository extends JpaRepository<Items, Long> {

    List<Items> findAllByInTopPageTrueOrderByAddedDateDesc();
    List<Items> findAllByNameLikeAndPriceBetweenOrderByPriceAsc(String name, double priceStart, double priceEnd);
    List<Items> findAllByNameLikeAndPriceBetweenOrderByPriceDesc(String name, double priceStart, double priceEnd);

}

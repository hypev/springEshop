package kz.app.hometask7.repositories;

import kz.app.hometask7.enitities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

}

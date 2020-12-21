package kz.app.hometask7.repositories;

import kz.app.hometask7.enitities.Items;
import kz.app.hometask7.enitities.Pictures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface PicturesRepository extends JpaRepository<Pictures, Long> {

    List<Pictures> findAllByItemEquals(Items item);

}

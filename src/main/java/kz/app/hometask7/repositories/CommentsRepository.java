package kz.app.hometask7.repositories;

import kz.app.hometask7.enitities.Comments;
import kz.app.hometask7.enitities.Items;
import kz.app.hometask7.enitities.Pictures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {

    List<Comments> findAllByItemEqualsOrderByAddedDateDesc(Items item);

}

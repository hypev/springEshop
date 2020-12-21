package kz.app.hometask7.repositories;

import kz.app.hometask7.enitities.Baskets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketsRepository extends JpaRepository<Baskets, Long> {

}

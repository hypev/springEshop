package kz.app.hometask7.repositories;

import kz.app.hometask7.enitities.Checkins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckinsRepository extends JpaRepository<Checkins, Long> {



}

package kz.app.hometask7.repositories;

import kz.app.hometask7.enitities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByEmail(String email);

}

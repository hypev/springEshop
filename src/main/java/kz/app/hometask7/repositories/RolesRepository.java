package kz.app.hometask7.repositories;

import kz.app.hometask7.enitities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface RolesRepository extends JpaRepository<Roles, Long> {
    Roles findByRole(String name);
}

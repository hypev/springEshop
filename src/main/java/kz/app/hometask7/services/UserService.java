package kz.app.hometask7.services;

import kz.app.hometask7.enitities.Roles;
import kz.app.hometask7.enitities.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<Users> getAllUsers();
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
    Users getUserByEmail(String email);
    Users createUser(Users user);
    Users updateUserFullname(Users user);
    Users updateUserPassword(Users user, String oldPassword, String newPassword);
    Users addUser(Users user);
    Users getUser(Long id);
    void deleteUser(Long id);
    Users saveUser(Users user);


    List<Roles> getAllRoles();
    Roles addRole(Roles role);
    Roles getRole(Long id);
    void deleteRole(Long id);
    void saveRole(Roles role);


}

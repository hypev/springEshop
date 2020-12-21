package kz.app.hometask7.services.implementation;

import kz.app.hometask7.enitities.Roles;
import kz.app.hometask7.enitities.Users;
import kz.app.hometask7.repositories.RolesRepository;
import kz.app.hometask7.repositories.UsersRepository;
import kz.app.hometask7.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Users user = usersRepository.findByEmail(email);
        if (user != null) {
            return new User(user.getEmail(), user.getPassword(), user.getRoles());
        }
        throw new UsernameNotFoundException("User Not Found");
    }

    @Override
    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public Users createUser(Users user) {
        Users checkUser = usersRepository.findByEmail(user.getEmail());
        if (checkUser == null) {
            Roles role = rolesRepository.findByRole("ROLE_USER");
            if (role != null) {
                ArrayList<Roles> roles = new ArrayList<>();
                roles.add(role);
                user.setRoles(roles);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                return usersRepository.save(user);
            }
        }
        return null;
    }

    @Override
    public Users updateUserFullname(Users user) {
        Users DBUser = usersRepository.findByEmail(user.getEmail());
        if (DBUser != null) {
            DBUser.setFullname(user.getFullname());
            return usersRepository.save(DBUser);
        }
        return null;
    }

    @Override
    public Users updateUserPassword(Users user, String oldPassword, String newPassword) {
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            Users checkUser = usersRepository.findByEmail(user.getEmail());
            if (checkUser != null) {
                checkUser.setPassword(passwordEncoder.encode(newPassword));
                return usersRepository.save(checkUser);
            }
        }
        return null;
    }

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public Users addUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    @Override
    public Users getUser(Long id) {
        return usersRepository.getOne(id);
    }

    @Override
    public void deleteUser(Long id) {
        Users user = usersRepository.getOne(id);
        if (user != null) {
            usersRepository.delete(user);
        }
    }

    @Override
    public Users saveUser(Users user) {
        Users saved = usersRepository.getOne(user.getId());
        if (saved != null) {
            saved.setFullname(user.getFullname());
            return usersRepository.save(saved);
        }
        return null;
    }

    @Override
    public List<Roles> getAllRoles() {
        return rolesRepository.findAll();
    }

    @Override
    public Roles addRole(Roles role) {
        return rolesRepository.save(role);
    }

    @Override
    public Roles getRole(Long id) {
        return rolesRepository.getOne(id);
    }

    @Override
    public void deleteRole(Long id) {
        Roles role = rolesRepository.getOne(id);
        if (role != null) {
            rolesRepository.delete(role);
        }
    }

    @Override
    public void saveRole(Roles role) {
        Roles saveRole = rolesRepository.getOne(role.getId());
        if (saveRole != null) {
            saveRole.setRole(role.getRole());
            saveRole.setDescription(role.getDescription());
            rolesRepository.save(saveRole);
        }
    }
}

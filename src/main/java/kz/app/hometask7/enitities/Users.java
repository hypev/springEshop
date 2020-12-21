package kz.app.hometask7.enitities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "picture_url")
    private String pictureUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Roles> roles;

    public String stringRoles() {
        String stringRoles = "";
            for (Roles r : roles) {
                stringRoles += r.getRole() + ", ";
            }
        return stringRoles.length() > 3 ? stringRoles.substring(0, stringRoles.length() - 2) : "";
    }

    public boolean isAdmin() {
        return stringRoles().contains("ROLE_ADMIN");
    }

    public boolean isModerator() {
        return stringRoles().contains("ROLE_MODERATOR");
    }

}

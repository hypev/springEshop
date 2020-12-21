package kz.app.hometask7.enitities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table (name = "brands")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brands {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private Long id;

    @Column (name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Countries country;
}

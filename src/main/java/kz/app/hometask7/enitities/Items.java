package kz.app.hometask7.enitities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Items {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description", length = 3000)
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "stars")
    private int stars;

    @Column(name = "small_pic_url")
    private String smallPicUrl;

    @Column(name = "large_pic_url")
    private String largePicUrl;

    @Column(name = "added_date",nullable = false, updatable = false)
    private Timestamp addedDate;

    @Column(name = "in_top_page")
    private boolean inTopPage;

    @ManyToOne(fetch = FetchType.LAZY)
    private Brands brand;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Categories> categories;
}

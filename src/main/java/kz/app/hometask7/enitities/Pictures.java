package kz.app.hometask7.enitities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "pictures")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pictures {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "added_date", nullable = false, updatable = false)
    private Timestamp addedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Items item;

}

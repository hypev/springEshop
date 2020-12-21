package kz.app.hometask7.enitities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "addedDate",nullable = false, updatable = false)
    private Timestamp addedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Items item;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users author;

    public String getDate() {
        return new SimpleDateFormat("HH:mm dd/MM/YYYY").format(addedDate);
    }

}

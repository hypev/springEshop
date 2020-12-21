package kz.app.hometask7.enitities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "checkins")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Checkins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fullname")
    private String fullname;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Orders> orders;


    public double totalSum() {
        double sum = 0;
        for (Orders o : orders)
            sum += o.getAmount() * o.getItem().getPrice();
        return sum;
    }

    public String totalItems() {
        String items = "";
        for (Orders o : orders)
            items += " | " + o.getItem().getName() + " x " + o.getAmount() + " | ";
        return items;
    }

}

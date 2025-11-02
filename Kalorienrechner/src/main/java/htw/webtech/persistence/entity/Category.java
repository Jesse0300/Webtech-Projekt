package htw.webtech.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // z. B. "Gemüse", "Fleisch", "Milchprodukte"

    @OneToMany(mappedBy = "category")
    private List<Food> foods;
}
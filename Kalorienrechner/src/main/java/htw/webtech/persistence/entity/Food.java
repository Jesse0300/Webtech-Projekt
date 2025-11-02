package htw.webtech.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double calories; // kcal per 100g
    private Double protein;  // g
    private Double carbs;    // g
    private Double fat;      // g

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}

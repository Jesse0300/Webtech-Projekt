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

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "calories") // kcal je 100g
    private Double calories;

    @Column(name = "protein", nullable = false)
    private Double protein;

    @Column(name = "carbs", nullable = false)
    private Double carbs;

    @Column(name = "fat", nullable = false)
    private Double fat;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}

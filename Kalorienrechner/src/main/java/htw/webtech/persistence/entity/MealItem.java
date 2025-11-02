package htw.webtech.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount; // in grams

    @ManyToOne
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    // Hilfsmethode zur Berechnung der Kalorien dieses Items
    public Double getTotalCalories() {
        if (food == null || food.getCalories() == null || amount == null) return 0.0;
        return (food.getCalories() / 100) * amount;
    }
}

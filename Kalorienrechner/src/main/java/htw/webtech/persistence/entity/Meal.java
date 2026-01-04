package htw.webtech.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal")
public class Meal extends BaseEntity {

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    @OneToMany(
            mappedBy = "meal",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MealItem> items = new ArrayList<>();

    // --------------------
    // Getter / Setter
    // --------------------

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public List<MealItem> getItems() {
        return items;
    }

    public void setItems(List<MealItem> items) {
        this.items = items;
    }

    // Convenience-Methoden (optional, aber sauber)
    public void addItem(MealItem item) {
        items.add(item);
        item.setMeal(this);
    }

    public void removeItem(MealItem item) {
        items.remove(item);
        item.setMeal(null);
    }
}

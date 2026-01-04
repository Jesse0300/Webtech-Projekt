package htw.webtech.persistence.repository;

import htw.webtech.persistence.entity.Meal;
import htw.webtech.persistence.entity.MealType;
import htw.webtech.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {

    Optional<Meal> findByUserAndDateAndMealType(User user, LocalDate date, MealType mealType);

    List<Meal> findAllByUserAndDate(User user, LocalDate date);
}

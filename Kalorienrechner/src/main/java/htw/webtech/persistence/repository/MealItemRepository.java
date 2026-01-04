package htw.webtech.persistence.repository;

import htw.webtech.persistence.entity.MealItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealItemRepository extends JpaRepository<MealItem, Long> {
}


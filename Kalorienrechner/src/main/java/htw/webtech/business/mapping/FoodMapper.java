package htw.webtech.business.mapping;

import htw.webtech.persistence.entity.Category;
import htw.webtech.persistence.entity.Food;
import htw.webtech.rest.model.FoodDTO;
import org.springframework.stereotype.Component;

@Component
public class FoodMapper {

    public FoodDTO toDto(Food f) {
        if (f == null) return null;
        return FoodDTO.builder()
                .id(f.getId())
                .name(f.getName())
                .calories(f.getCalories())
                .protein(f.getProtein())
                .carbs(f.getCarbs())
                .fat(f.getFat())
                .categoryId(f.getCategory() != null ? f.getCategory().getId() : null)
                .categoryName(f.getCategory() != null ? f.getCategory().getName() : null)
                .build();
    }

    public void updateEntityFromDto(FoodDTO dto, Food entity, Category categoryOrNull) {
        entity.setName(dto.getName());
        entity.setCalories(dto.getCalories());
        entity.setProtein(dto.getProtein());
        entity.setCarbs(dto.getCarbs());
        entity.setFat(dto.getFat());
        entity.setCategory(categoryOrNull);
    }
}

package htw.webtech.business.service;

import htw.webtech.persistence.entity.*;
import htw.webtech.persistence.repository.*;
import htw.webtech.rest.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final CurrentUserService currentUserService;

    public MealService(MealRepository mealRepository,
                       CurrentUserService currentUserService) {
        this.mealRepository = mealRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public MealsDayDTO getDay(LocalDate date) {
        User user = currentUserService.requireUser();

        List<Meal> meals = mealRepository.findAllByUserAndDate(user, date);

        Map<MealType, MealSummaryDTO> grouped = new EnumMap<>(MealType.class);

        for (MealType type : MealType.values()) {

            List<MealItem> items = meals.stream()
                    .filter(m -> m.getMealType() == type)
                    .flatMap(m -> m.getItems().stream())
                    .toList();

            double totalCalories = 0;
            List<MealItemDTO> dtoItems = new ArrayList<>();

            for (MealItem i : items) {
                Food f = i.getFood();
                double grams = i.getAmount() != null ? i.getAmount() : 0.0;
                double factor = grams / 100.0;

                double calories = i.getTotalCalories();
                double carbs = f != null && f.getCarbs() != null ? f.getCarbs() * factor : 0;
                double fat = f != null && f.getFat() != null ? f.getFat() * factor : 0;
                double protein = f != null && f.getProtein() != null ? f.getProtein() * factor : 0;

                totalCalories += calories;

                dtoItems.add(new MealItemDTO(
                        i.getId(),
                        f != null ? f.getId() : null,
                        f != null ? f.getName() : "",
                        grams,
                        calories,
                        carbs,
                        fat,
                        protein
                ));
            }

            grouped.put(type, new MealSummaryDTO(totalCalories, dtoItems));
        }

        double dayTotal = grouped.values().stream()
                .mapToDouble(MealSummaryDTO::totalCalories)
                .sum();

        return new MealsDayDTO(date, dayTotal, grouped);
    }
}

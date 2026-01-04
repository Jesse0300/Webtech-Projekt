package htw.webtech.business.service;

import htw.webtech.persistence.entity.Food;
import htw.webtech.persistence.entity.Meal;
import htw.webtech.persistence.entity.MealItem;
import htw.webtech.persistence.entity.MealType;
import htw.webtech.persistence.repository.FoodRepository;
import htw.webtech.persistence.repository.MealItemRepository;
import htw.webtech.persistence.repository.MealRepository;
import htw.webtech.rest.model.AddMealItemRequest;
import htw.webtech.rest.model.MealItemDTO;
import htw.webtech.rest.model.MealSummaryDTO;
import htw.webtech.rest.model.MealsDayDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final MealItemRepository mealItemRepository;
    private final FoodRepository foodRepository;

    public MealService(MealRepository mealRepository,
                       MealItemRepository mealItemRepository,
                       FoodRepository foodRepository) {
        this.mealRepository = mealRepository;
        this.mealItemRepository = mealItemRepository;
        this.foodRepository = foodRepository;
    }

    // ---------------------------------------------------------------------
    // ADD ITEM (POST /api/meals/items)
    // ---------------------------------------------------------------------
    @Transactional
    public void addItem(AddMealItemRequest req) {

        Food food = foodRepository.findById(req.foodId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Food not found: " + req.foodId()
                ));

        Meal meal = mealRepository
                .findByDateAndMealType(req.date(), req.mealType())
                .orElseGet(() -> {
                    Meal m = new Meal();
                    m.setDate(req.date());
                    m.setMealType(req.mealType());
                    return mealRepository.save(m);
                });

        MealItem item = new MealItem();
        item.setMeal(meal);
        item.setFood(food);

        // ⚠️ Annahme: Feld heißt bei dir "amount"
        item.setAmount(req.amountGrams());

        mealItemRepository.save(item);
    }

    // ---------------------------------------------------------------------
    // GET DAY (GET /api/meals/day?date=YYYY-MM-DD)
    // ---------------------------------------------------------------------
    @Transactional(readOnly = true)
    public MealsDayDTO getDay(LocalDate date) {

        List<Meal> meals = mealRepository.findAllByDate(date);

        // Initialisiere alle MealTypes (damit im Frontend nichts fehlt)
        Map<MealType, List<MealItemDTO>> itemsByType =
                new EnumMap<>(MealType.class);

        for (MealType t : MealType.values()) {
            itemsByType.put(t, new ArrayList<>());
        }

        // Sammle Items
        for (Meal meal : meals) {
            MealType type = meal.getMealType();

            for (MealItem item : meal.getItems()) {
                Food food = item.getFood();

                double grams = item.getAmount();
                double calories = calcCalories(food.getCalories(), grams);

                itemsByType.get(type).add(
                        new MealItemDTO(
                                item.getId(),
                                food.getId(),
                                food.getName(),
                                grams,
                                calories
                        )
                );
            }
        }

        // Erzeuge Summary
        Map<MealType, MealSummaryDTO> summary =
                new EnumMap<>(MealType.class);

        double totalCalories = 0.0;

        for (MealType t : MealType.values()) {
            List<MealItemDTO> list = itemsByType.get(t);

            double sum = list.stream()
                    .mapToDouble(MealItemDTO::calories)
                    .sum();

            sum = round2(sum);
            totalCalories += sum;

            summary.put(t, new MealSummaryDTO(sum, list));
        }

        totalCalories = round2(totalCalories);

        return new MealsDayDTO(date, totalCalories, summary);
    }

    // ---------------------------------------------------------------------
    // HELPER
    // ---------------------------------------------------------------------
    private double calcCalories(double caloriesPer100g, double grams) {
        return round2((caloriesPer100g / 100.0) * grams);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

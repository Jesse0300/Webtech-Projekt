package htw.webtech.business.service;

import htw.webtech.persistence.entity.Food;
import htw.webtech.persistence.entity.Meal;
import htw.webtech.persistence.entity.MealItem;
import htw.webtech.rest.model.MealType;
import htw.webtech.persistence.entity.User;
import htw.webtech.persistence.repository.FoodRepository;
import htw.webtech.persistence.repository.MealItemRepository;
import htw.webtech.persistence.repository.MealRepository;
import htw.webtech.rest.model.AddMealItemRequest;
import htw.webtech.rest.model.MealItemDTO;
import htw.webtech.rest.model.MealSummaryDTO;
import htw.webtech.rest.model.MealsDayDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class MealService {

    private final FoodRepository foodRepository;
    private final MealRepository mealRepository;
    private final MealItemRepository mealItemRepository;
    private final CurrentUserService currentUserService;

    public MealService(
            FoodRepository foodRepository,
            MealRepository mealRepository,
            MealItemRepository mealItemRepository,
            CurrentUserService currentUserService
    ) {
        this.foodRepository = foodRepository;
        this.mealRepository = mealRepository;
        this.mealItemRepository = mealItemRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * GET /api/meals/day?date=YYYY-MM-DD
     */
    @Transactional(readOnly = true)
    public MealsDayDTO getDay(LocalDate date) {
        User user = currentUserService.requireUser();

        // Erwartet: List<Meal> findAllByUserAndDate(User user, LocalDate date)
        List<Meal> meals = mealRepository.findAllByUserAndDate(user, date);

        Map<MealType, MealSummaryDTO> grouped = new EnumMap<>(MealType.class);

        for (MealType t : MealType.values()) {
            List<MealItem> items = meals.stream()
                    .filter(m -> m.getMealType() == t)
                    .flatMap(m -> m.getItems().stream())
                    .toList();

            double total = items.stream().mapToDouble(MealItem::getTotalCalories).sum();

            List<MealItemDTO> dtoItems = items.stream()
                    .map(i -> new MealItemDTO(
                            i.getId(),
                            i.getFood() != null ? i.getFood().getId() : null,
                            i.getFood() != null ? i.getFood().getName() : "",
                            i.getAmount() != null ? i.getAmount() : 0.0,      // amount == Gramm
                            i.getTotalCalories() != null ? i.getTotalCalories() : 0.0
                    ))
                    .toList();

            grouped.put(t, new MealSummaryDTO(total, dtoItems));
        }

        double dayTotal = grouped.values().stream()
                .mapToDouble(MealSummaryDTO::totalCalories)
                .sum();

        // ✅ deine DTO-Signatur: (date, totalCalories, meals)
        return new MealsDayDTO(date, dayTotal, grouped);
    }

    /**
     * POST /api/meals/items
     */
    @Transactional
    public void addItem(AddMealItemRequest req) {
        User user = currentUserService.requireUser();

        Food food = foodRepository.findById(req.foodId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found: " + req.foodId()));

        // Erwartet: Optional<Meal> findByUserAndDateAndMealType(User user, LocalDate date, MealType mealType)
        Meal meal = mealRepository.findByUserAndDateAndMealType(user, req.date(), req.mealType())
                .orElseGet(() -> {
                    Meal m = new Meal();
                    m.setUser(user);
                    m.setDate(req.date());
                    m.setMealType(req.mealType());
                    return mealRepository.save(m);
                });

        MealItem item = new MealItem();
        item.setMeal(meal);
        item.setFood(food);

        // req.amountGrams() ist Gramm – in Entity heißt es amount
        item.setAmount(req.amountGrams());

        mealItemRepository.save(item);
    }

    /**
     * DELETE /api/meals/items/{id}
     */
    @Transactional
    public void deleteItem(Long mealItemId) {
        User user = currentUserService.requireUser();

        MealItem item = mealItemRepository.findById(mealItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MealItem not found: " + mealItemId));

        Meal meal = item.getMeal();
        if (meal == null || meal.getUser() == null || !meal.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }

        // Entfernen + speichern (sauber, falls orphanRemoval aktiv ist)
        meal.getItems().remove(item);
        mealRepository.save(meal);

        // Optional: wenn Meal danach leer ist -> löschen
        if (meal.getItems().isEmpty()) {
            mealRepository.delete(meal);
        }
    }
}

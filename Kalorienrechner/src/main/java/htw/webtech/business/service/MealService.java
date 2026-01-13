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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class MealService {

    private final FoodRepository foodRepository;
    private final MealRepository mealRepository;
    private final MealItemRepository mealItemRepository;
    private final CurrentUserService currentUserService;

    public MealService(FoodRepository foodRepository,
                       MealRepository mealRepository,
                       MealItemRepository mealItemRepository,
                       CurrentUserService currentUserService) {
        this.foodRepository = foodRepository;
        this.mealRepository = mealRepository;
        this.mealItemRepository = mealItemRepository;
        this.currentUserService = currentUserService;
    }

    // ✅ POST /api/meals/items
    @Transactional
    public void addItem(AddMealItemRequest req) {
        User user = currentUserService.requireUser();

        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request missing");
        }
        if (req.date() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date missing");
        }
        if (req.mealType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mealType missing");
        }
        if (req.foodId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "foodId missing");
        }
        if (req.amountGrams() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amountGrams must be > 0");
        }

        Food food = foodRepository.findById(req.foodId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found"));

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
        item.setAmount(req.amountGrams());

        mealItemRepository.save(item);
    }

    // ✅ DELETE /api/meals/items/{id}
    @Transactional
    public void deleteItem(Long id) {
        User user = currentUserService.requireUser();

        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id missing");
        }

        MealItem item = mealItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MealItem not found"));

        Meal meal = item.getMeal();
        if (meal == null || meal.getUser() == null || meal.getUser().getId() == null
                || !meal.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }

        // sauber entfernen
        meal.getItems().remove(item);
        mealRepository.save(meal);
        mealItemRepository.delete(item);

        // Optional: leere Mahlzeit entfernen
        if (meal.getItems().isEmpty()) {
            mealRepository.delete(meal);
        }
    }

    // ✅ GET /api/meals/day?date=YYYY-MM-DD  (inkl. Makros)
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

            double totalCalories = 0.0;
            List<MealItemDTO> dtoItems = new ArrayList<>();

            for (MealItem i : items) {
                Food f = i.getFood();
                double grams = i.getAmount() != null ? i.getAmount() : 0.0;
                double factor = grams / 100.0;

                double calories = i.getTotalCalories();
                double carbs = (f != null && f.getCarbs() != null) ? f.getCarbs() * factor : 0.0;
                double fat = (f != null && f.getFat() != null) ? f.getFat() * factor : 0.0;
                double protein = (f != null && f.getProtein() != null) ? f.getProtein() * factor : 0.0;

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

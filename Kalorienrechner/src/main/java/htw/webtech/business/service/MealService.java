package htw.webtech.business.service;

import htw.webtech.persistence.entity.Food;
import htw.webtech.persistence.entity.Meal;
import htw.webtech.persistence.entity.MealItem;
import htw.webtech.persistence.entity.MealType;
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
import java.util.stream.Collectors;

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

    @Transactional
    public void addItem(AddMealItemRequest req) {
        User user = currentUserService.requireUser();

        Food food = foodRepository.findById(req.foodId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found: " + req.foodId()));

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

    @Transactional(readOnly = true)
    public MealsDayDTO getDay(LocalDate date) {
        User user = currentUserService.requireUser();

        List<Meal> meals = mealRepository.findAllByUserAndDate(user, date);

        Map<MealType, MealSummaryDTO> grouped = new EnumMap<>(MealType.class);
        for (MealType t : MealType.values()) {
            grouped.put(t, new MealSummaryDTO(0.0, List.of()));
        }

        for (MealType t : MealType.values()) {
            List<MealItem> items = meals.stream()
                    .filter(m -> m.getMealType() == t)
                    .flatMap(m -> m.getItems().stream())
                    .collect(Collectors.toList());

            List<MealItemDTO> itemDtos = items.stream()
                    .map(i -> new MealItemDTO(
                            i.getId(),
                            i.getFood().getId(),
                            i.getFood().getName(),
                            i.getAmount() == null ? 0.0 : i.getAmount(),
                            round2(i.getTotalCalories() == null ? 0.0 : i.getTotalCalories())
                    ))
                    .toList();

            double total = itemDtos.stream().mapToDouble(MealItemDTO::calories).sum();
            grouped.put(t, new MealSummaryDTO(round2(total), itemDtos));
        }

        double totalCalories = grouped.values().stream().mapToDouble(MealSummaryDTO::totalCalories).sum();
        return new MealsDayDTO(date, round2(totalCalories), grouped);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}

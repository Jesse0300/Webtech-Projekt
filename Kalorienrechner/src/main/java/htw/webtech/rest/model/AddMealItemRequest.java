package htw.webtech.rest.model;

import java.time.LocalDate;

public record AddMealItemRequest(
        LocalDate date,
        MealType mealType,
        Long foodId,
        double amountGrams
) {}

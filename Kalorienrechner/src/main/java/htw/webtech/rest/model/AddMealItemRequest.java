package htw.webtech.rest.model;

import htw.webtech.persistence.entity.MealType;

import java.time.LocalDate;

public record AddMealItemRequest(
        LocalDate date,
        MealType mealType,
        Long foodId,
        double amountGrams
) {}

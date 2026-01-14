package htw.webtech.rest.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record AddMealItemRequest(
        @NotNull LocalDate date,
        @NotNull MealType mealType,
        @NotNull Long foodId,
        @Positive double amountGrams
) {}

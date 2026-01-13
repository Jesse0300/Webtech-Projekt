
package htw.webtech.rest.model;

public record MealItemDTO(
        Long id,
        Long foodId,
        String foodName,
        double amountGrams,
        double calories
) {}


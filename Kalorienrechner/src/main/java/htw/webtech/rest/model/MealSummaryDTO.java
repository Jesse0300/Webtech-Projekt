package htw.webtech.rest.model;

import java.util.List;

public record MealSummaryDTO(
        double totalCalories,
        List<MealItemDTO> items
) {}
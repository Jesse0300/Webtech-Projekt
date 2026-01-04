package htw.webtech.rest.model;

import htw.webtech.persistence.entity.MealType;

import java.time.LocalDate;
import java.util.Map;

public record MealsDayDTO(
        LocalDate date,
        double totalCalories,
        Map<MealType, MealSummaryDTO> meals
) {}

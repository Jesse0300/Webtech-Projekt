package htw.webtech.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotNull @PositiveOrZero
    private Double calories;

    @NotNull @PositiveOrZero
    private Double protein;

    @NotNull @PositiveOrZero
    private Double carbs;

    @NotNull @PositiveOrZero
    private Double fat;

    private Long categoryId;
    private String categoryName;
}

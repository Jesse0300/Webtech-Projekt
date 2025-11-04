package htw.webtech.rest.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDTO {
    private Long id;
    private String name;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;

    private Long categoryId;     // <--- Diese Zeile ist entscheidend
    private String categoryName;
}
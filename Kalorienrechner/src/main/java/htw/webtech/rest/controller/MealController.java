package htw.webtech.rest.controller;

import htw.webtech.business.service.MealService;
import htw.webtech.rest.model.AddMealItemRequest;
import htw.webtech.rest.model.MealsDayDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    /**
     * Add item to a meal (breakfast/lunch/dinner/snack) for a given date.
     * POST /api/meals/items
     */
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public void addItem(@RequestBody AddMealItemRequest req) {
        mealService.addItem(req);
    }

    /**
     * Delete a meal item by id.
     * DELETE /api/meals/items/{id}
     */
    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        mealService.deleteItem(id);
    }

    /**
     * Get all meals for a day grouped by mealType.
     * GET /api/meals/day?date=YYYY-MM-DD
     */
    @GetMapping("/day")
    public MealsDayDTO getDay(@RequestParam LocalDate date) {
        return mealService.getDay(date);
    }
}

package htw.webtech.rest.controller;

import htw.webtech.business.service.MealService;
import htw.webtech.rest.model.AddMealItemRequest;
import htw.webtech.rest.model.MealsDayDTO;
import jakarta.validation.Valid;
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

    // ✅ POST /api/meals/items
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addItem(@Valid @RequestBody AddMealItemRequest req) {
        mealService.addItem(req);
    }

    // ✅ DELETE /api/meals/items/{id}
    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        mealService.deleteItem(id);
    }

    // ✅ GET /api/meals/day?date=YYYY-MM-DD
    @GetMapping("/day")
    public MealsDayDTO getDay(@RequestParam LocalDate date) {
        return mealService.getDay(date);
    }
}

package htw.webtech.rest.controller;

import htw.webtech.business.service.FoodService;
import htw.webtech.rest.model.FoodDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/foods")
    public ResponseEntity<List<FoodDTO>> getFoods() {
        return ResponseEntity.ok(foodService.getAllFoods());
    }
}

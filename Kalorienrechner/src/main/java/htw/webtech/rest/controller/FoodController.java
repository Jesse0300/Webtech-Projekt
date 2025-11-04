package htw.webtech.rest.controller;

import htw.webtech.business.service.FoodService;
import htw.webtech.rest.model.FoodDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping("/{id}")
    public FoodDTO get(@PathVariable Long id) {
        return foodService.get(id);
    }

    @GetMapping
    public List<FoodDTO> list() {
        return foodService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoodDTO create(@RequestBody FoodDTO dto) {
        return foodService.create(dto);
    }

    @PutMapping("/{id}")
    public FoodDTO update(@PathVariable Long id, @RequestBody FoodDTO dto) {
        return foodService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        foodService.delete(id);
    }
}
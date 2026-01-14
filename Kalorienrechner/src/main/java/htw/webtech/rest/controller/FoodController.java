package htw.webtech.rest.controller;

import htw.webtech.business.service.FoodService;
import htw.webtech.rest.model.FoodDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    // ✅ GET /api/foods
    @GetMapping
    public List<FoodDTO> getAll() {
        return foodService.list(); // ✅ passt zu deinem FoodService
    }

    // ✅ GET /api/foods/{id}
    @GetMapping("/{id}")
    public FoodDTO getOne(@PathVariable Long id) {
        return foodService.get(id); // ✅ passt zu deinem FoodService
    }

    // ✅ POST /api/foods
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoodDTO create(@RequestBody FoodDTO dto) {
        validateFoodDto(dto);
        return foodService.create(dto);
    }

    // ✅ PUT /api/foods/{id}
    @PutMapping("/{id}")
    public FoodDTO update(@PathVariable Long id, @RequestBody FoodDTO dto) {
        validateFoodDto(dto);
        return foodService.update(id, dto);
    }

    // ✅ DELETE /api/foods/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        foodService.delete(id);
    }

    // ✅ Input-Validierung (Controller-only, keine DTO-Änderungen nötig)
    private void validateFoodDto(FoodDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body fehlt");
        }

        String name = dto.getName() != null ? dto.getName().trim() : "";
        if (name.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name darf nicht leer sein");
        }

        if (dto.getCalories() == null || dto.getProtein() == null || dto.getCarbs() == null || dto.getFat() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kalorien und Makros dürfen nicht null sein");
        }

        if (dto.getCalories() < 0 || dto.getProtein() < 0 || dto.getCarbs() < 0 || dto.getFat() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kalorien und Makros dürfen nicht negativ sein");
        }

        // Optional: sanity checks (kannst du rausnehmen, wenn du willst)
        if (dto.getCalories() > 10000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kalorienwert ist unrealistisch hoch");
        }
    }
}

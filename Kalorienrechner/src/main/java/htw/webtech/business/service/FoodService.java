package htw.webtech.business.service;

import htw.webtech.rest.model.FoodDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService {

    public List<FoodDTO> getAllFoods() {
        return List.of(
                new FoodDTO("Apfel", 52),
                new FoodDTO("Banane", 89),
                new FoodDTO("Pizza (100g)", 266)
        );
    }
}

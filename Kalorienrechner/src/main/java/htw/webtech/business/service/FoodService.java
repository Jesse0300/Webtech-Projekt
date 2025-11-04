package htw.webtech.business.service;

import htw.webtech.persistence.entity.Category;
import htw.webtech.persistence.entity.Food;
import htw.webtech.persistence.repository.CategoryRepository;
import htw.webtech.persistence.repository.FoodRepository;
import htw.webtech.business.mapping.FoodMapper;
import htw.webtech.rest.model.FoodDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodService {

    private final FoodRepository foodRepo;
    private final CategoryRepository categoryRepo;
    private final FoodMapper mapper;

    @Transactional(readOnly = true)
    public FoodDTO get(Long id) {
        Food f = foodRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food " + id + " not found"));
        return mapper.toDto(f);
    }

    @Transactional(readOnly = true)
    public List<FoodDTO> list() {
        return foodRepo.findAll().stream().map(mapper::toDto).toList();
    }

    public FoodDTO create(FoodDTO dto) {
        Category cat = null;
        if (dto.getCategoryId() != null) {
            cat = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found: " + dto.getCategoryId()));
        }
        Food entity = Food.builder().build();
        mapper.updateEntityFromDto(dto, entity, cat);
        Food saved = foodRepo.save(entity);
        return mapper.toDto(saved);
    }

    public FoodDTO update(Long id, FoodDTO dto) {
        Food entity = foodRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food " + id + " not found"));

        Category cat = null;
        if (dto.getCategoryId() != null) {
            cat = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found: " + dto.getCategoryId()));
        }
        mapper.updateEntityFromDto(dto, entity, cat);
        return mapper.toDto(entity); // dank @Transactional wird dirty checking speichern
    }

    public void delete(Long id) {
        if (!foodRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Food " + id + " not found");
        }
        foodRepo.deleteById(id);
    }
}
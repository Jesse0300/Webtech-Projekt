package htw.webtech.business.service;

import htw.webtech.business.mapping.FoodMapper;
import htw.webtech.persistence.entity.Category;
import htw.webtech.persistence.entity.Food;
import htw.webtech.persistence.repository.CategoryRepository;
import htw.webtech.persistence.repository.FoodRepository;
import htw.webtech.rest.model.FoodDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodServiceTest {

    private FoodRepository foodRepo;
    private CategoryRepository categoryRepo;
    private FoodMapper mapper;

    private FoodService service;

    @BeforeEach
    void setUp() {
        foodRepo = mock(FoodRepository.class);
        categoryRepo = mock(CategoryRepository.class);
        mapper = new FoodMapper();

        service = new FoodService(foodRepo, categoryRepo, mapper);
    }

    @Test
    void get_returnsDto_whenFoodExists() {
        Food f = Food.builder()
                .id(1L).name("Apple")
                .calories(52.0).protein(0.3).carbs(14.0).fat(0.2)
                .build();

        when(foodRepo.findById(1L)).thenReturn(Optional.of(f));

        FoodDTO dto = service.get(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Apple");
        assertThat(dto.getCalories()).isEqualTo(52.0);
    }

    @Test
    void list_returnsDtos() {
        Food a = Food.builder().id(1L).name("A").calories(10.0).protein(1.0).carbs(1.0).fat(1.0).build();
        Food b = Food.builder().id(2L).name("B").calories(20.0).protein(2.0).carbs(2.0).fat(2.0).build();
        when(foodRepo.findAll()).thenReturn(List.of(a, b));

        List<FoodDTO> res = service.list();

        assertThat(res).hasSize(2);
        assertThat(res.get(0).getName()).isEqualTo("A");
        assertThat(res.get(1).getName()).isEqualTo("B");
    }

    @Test
    void create_savesFood_andAttachesCategory_whenCategoryIdProvided() {
        Category cat = Category.builder().id(9L).name("Fruit").build();
        when(categoryRepo.findById(9L)).thenReturn(Optional.of(cat));

        when(foodRepo.save(any(Food.class))).thenAnswer(inv -> {
            Food saved = inv.getArgument(0, Food.class);
            saved.setId(123L);
            return saved;
        });

        FoodDTO dto = FoodDTO.builder()
                .name("Banana").calories(89.0).protein(1.1).carbs(23.0).fat(0.3)
                .categoryId(9L)
                .build();

        FoodDTO out = service.create(dto);

        assertThat(out.getId()).isEqualTo(123L);
        assertThat(out.getCategoryId()).isEqualTo(9L);
        assertThat(out.getCategoryName()).isEqualTo("Fruit");

        ArgumentCaptor<Food> captor = ArgumentCaptor.forClass(Food.class);
        verify(foodRepo).save(captor.capture());
        assertThat(captor.getValue().getCategory().getId()).isEqualTo(9L);
    }

    @Test
    void update_updatesExistingFood_andReturnsDto() {
        Food existing = Food.builder()
                .id(5L).name("Old")
                .calories(1.0).protein(1.0).carbs(1.0).fat(1.0)
                .build();

        when(foodRepo.findById(5L)).thenReturn(Optional.of(existing));

        Category cat = Category.builder().id(7L).name("NewCat").build();
        when(categoryRepo.findById(7L)).thenReturn(Optional.of(cat));

        FoodDTO dto = FoodDTO.builder()
                .name("New")
                .calories(100.0).protein(10.0).carbs(20.0).fat(30.0)
                .categoryId(7L)
                .build();

        FoodDTO out = service.update(5L, dto);

        assertThat(out.getId()).isEqualTo(5L);
        assertThat(out.getName()).isEqualTo("New");
        assertThat(out.getCategoryId()).isEqualTo(7L);
        assertThat(existing.getName()).isEqualTo("New"); // dirty-checking in service
    }

    @Test
    void delete_deletes_whenExists_elseThrows404() {
        when(foodRepo.existsById(10L)).thenReturn(true);

        service.delete(10L);

        verify(foodRepo).deleteById(10L);

        when(foodRepo.existsById(11L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(11L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }
}

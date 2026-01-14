package htw.webtech.business.service;

import htw.webtech.persistence.entity.Food;
import htw.webtech.persistence.entity.Meal;
import htw.webtech.persistence.entity.MealItem;
import htw.webtech.persistence.entity.User;
import htw.webtech.persistence.repository.FoodRepository;
import htw.webtech.persistence.repository.MealItemRepository;
import htw.webtech.persistence.repository.MealRepository;
import htw.webtech.rest.model.AddMealItemRequest;
import htw.webtech.rest.model.MealType;
import htw.webtech.rest.model.MealsDayDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MealServiceTest {

    private FoodRepository foodRepository;
    private MealRepository mealRepository;
    private MealItemRepository mealItemRepository;
    private CurrentUserService currentUserService;

    private MealService service;

    private User user;

    @BeforeEach
    void setUp() {
        foodRepository = mock(FoodRepository.class);
        mealRepository = mock(MealRepository.class);
        mealItemRepository = mock(MealItemRepository.class);
        currentUserService = mock(CurrentUserService.class);

        service = new MealService(foodRepository, mealRepository, mealItemRepository, currentUserService);

        user = new User();
        user.setId(1L);
        user.setUsername("u");
        when(currentUserService.requireUser()).thenReturn(user);
    }

    @Test
    void addItem_valid_createsMealIfMissing_andSavesMealItem() {
        LocalDate date = LocalDate.of(2026, 1, 14);
        Food food = Food.builder().id(99L).name("Rice").calories(100.0).protein(2.0).carbs(22.0).fat(1.0).build();
        when(foodRepository.findById(99L)).thenReturn(Optional.of(food));

        when(mealRepository.findByUserAndDateAndMealType(user, date, MealType.LUNCH))
                .thenReturn(Optional.empty());

        when(mealRepository.save(any(Meal.class))).thenAnswer(inv -> inv.getArgument(0, Meal.class));

        when(mealItemRepository.save(any(MealItem.class))).thenAnswer(inv -> inv.getArgument(0, MealItem.class));

        service.addItem(new AddMealItemRequest(date, MealType.LUNCH, 99L, 150.0));

        verify(mealRepository).save(any(Meal.class));
        verify(mealItemRepository).save(any(MealItem.class));
    }

    @Test
    void addItem_invalid_missingDate_throws400() {
        assertThatThrownBy(() -> service.addItem(new AddMealItemRequest(null, MealType.BREAKFAST, 1L, 100)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
    }

    @Test
    void deleteItem_forbiddenIfNotOwner() {
        Meal meal = new Meal();
        User other = new User();
        other.setId(999L);
        meal.setUser(other);

        MealItem item = new MealItem();
        item.setId(7L);
        item.setMeal(meal);

        when(mealItemRepository.findById(7L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> service.deleteItem(7L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                });
    }

    @Test
    void getDay_aggregatesCalories_forMealsAndItems() {
        LocalDate date = LocalDate.of(2026, 1, 14);

        Food chicken = Food.builder().id(1L).name("Chicken").calories(200.0).protein(30.0).carbs(0.0).fat(8.0).build();
        MealItem i1 = new MealItem();
        i1.setId(1L);
        i1.setFood(chicken);
        i1.setAmount(150.0); // 150g -> calories = 200/100*150 = 300

        Meal m = new Meal();
        m.setUser(user);
        m.setDate(date);
        m.setMealType(MealType.DINNER);
        m.setItems(List.of(i1));

        when(mealRepository.findAllByUserAndDate(user, date)).thenReturn(List.of(m));

        MealsDayDTO dto = service.getDay(date);

        assertThat(dto.date()).isEqualTo(date);
        assertThat(dto.totalCalories()).isCloseTo(300.0, within(0.001));
        assertThat(dto.meals().get(MealType.DINNER).totalCalories()).isCloseTo(300.0, within(0.001));
        assertThat(dto.meals().get(MealType.DINNER).items()).hasSize(1);
    }
}

package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.FoodLog;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.MealType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never"
})
class FoodLogRepositoryTest {

    @Autowired
    private FoodLogRepository foodLogRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail(username + "@test.com");

        return entityManager.persist(user);
    }

    private FoodLog createFoodLog(
            User user,
            LocalDate date,
            String foodName,
            double calories) {

        FoodLog foodLog = new FoodLog();

        foodLog.setUser(user);
        foodLog.setFdcId(100L);
        foodLog.setFoodName(foodName);
        foodLog.setQuantity(100.0);
        foodLog.setUnit("g");
        foodLog.setMealType(MealType.BREAKFAST);
        foodLog.setCalories(calories);
        foodLog.setProtein(20.0);
        foodLog.setCarbohydrates(10.0);
        foodLog.setFat(5.0);
        foodLog.setFiber(2.0);
        foodLog.setSugar(1.0);
        foodLog.setSodium(100.0);
        foodLog.setLogDate(date);
        foodLog.setLoggedAt(LocalDateTime.now());

        return foodLog;
    }

    @Test
    void findByUserAndLogDate_returnsOnlyLogsForGivenDate() {
        User user = createUser("user1");

        LocalDate today = LocalDate.of(2026, 9, 7);
        LocalDate yesterday = today.minusDays(1);

        foodLogRepository.save(
                createFoodLog(user, today, "Chicken", 200)
        );

        foodLogRepository.save(
                createFoodLog(user, yesterday, "Rice", 300)
        );

        List<FoodLog> result =
                foodLogRepository.findByUserAndLogDate(user, today);

        assertEquals(1, result.size());
        assertEquals("Chicken", result.get(0).getFoodName());
    }

    @Test
    void findByUserAndLogDate_doesNotReturnOtherUsersLogs() {
        User user1 = createUser("user1");
        User user2 = createUser("user2");

        LocalDate today = LocalDate.of(2026, 9, 7);

        foodLogRepository.save(
                createFoodLog(user1, today, "Chicken", 200)
        );

        foodLogRepository.save(
                createFoodLog(user2, today, "Rice", 300)
        );

        List<FoodLog> result =
                foodLogRepository.findByUserAndLogDate(user1, today);

        assertEquals(1, result.size());
        assertEquals("Chicken", result.get(0).getFoodName());
    }

    @Test
    void findByUserAndLogDate_returnsEmptyWhenNoLogsExist() {
        User user = createUser("user1");

        List<FoodLog> result =
                foodLogRepository.findByUserAndLogDate(
                        user,
                        LocalDate.of(2026, 9, 7)
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserAndLogDateBetweenOrderByLogDateDesc_returnsLogsInDescendingDateOrder() {
        User user = createUser("user1");

        LocalDate date1 = LocalDate.of(2026, 9, 1);
        LocalDate date2 = LocalDate.of(2026, 9, 5);
        LocalDate date3 = LocalDate.of(2026, 9, 3);

        foodLogRepository.save(
                createFoodLog(user, date1, "Food 1", 100)
        );

        foodLogRepository.save(
                createFoodLog(user, date2, "Food 2", 200)
        );

        foodLogRepository.save(
                createFoodLog(user, date3, "Food 3", 300)
        );

        List<FoodLog> result =
                foodLogRepository
                        .findByUserAndLogDateBetweenOrderByLogDateDesc(
                                user,
                                date1,
                                date3
                        );

        assertEquals(2, result.size());

        assertEquals("Food 3", result.get(0).getFoodName());
        assertEquals("Food 1", result.get(1).getFoodName());
    }

    @Test
    void findByUserAndLogDateBetweenOrderByLogDateDesc_includesBoundaryDates() {
        User user = createUser("user1");

        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 5);

        foodLogRepository.save(
                createFoodLog(user, start, "Start Food", 100)
        );

        foodLogRepository.save(
                createFoodLog(user, end, "End Food", 200)
        );

        List<FoodLog> result =
                foodLogRepository
                        .findByUserAndLogDateBetweenOrderByLogDateDesc(
                                user,
                                start,
                                end
                        );

        assertEquals(2, result.size());
        assertEquals("End Food", result.get(0).getFoodName());
        assertEquals("Start Food", result.get(1).getFoodName());
    }

    @Test
    void findByUserAndLogDateBetweenOrderByLogDateDesc_doesNotReturnOtherUsersLogs() {
        User user1 = createUser("user1");
        User user2 = createUser("user2");

        LocalDate date = LocalDate.of(2026, 9, 5);

        foodLogRepository.save(
                createFoodLog(user1, date, "User 1 Food", 100)
        );

        foodLogRepository.save(
                createFoodLog(user2, date, "User 2 Food", 200)
        );

        List<FoodLog> result =
                foodLogRepository
                        .findByUserAndLogDateBetweenOrderByLogDateDesc(
                                user1,
                                date,
                                date
                        );

        assertEquals(1, result.size());
        assertEquals("User 1 Food", result.get(0).getFoodName());
    }

    @Test
    void findByUserAndLogDateBetweenOrderByLogDateDesc_returnsEmptyOutsideRange() {
        User user = createUser("user1");

        foodLogRepository.save(
                createFoodLog(
                        user,
                        LocalDate.of(2026, 9, 10),
                        "Food",
                        100
                )
        );

        List<FoodLog> result =
                foodLogRepository
                        .findByUserAndLogDateBetweenOrderByLogDateDesc(
                                user,
                                LocalDate.of(2026, 9, 1),
                                LocalDate.of(2026, 9, 5)
                        );

        assertTrue(result.isEmpty());
    }
}
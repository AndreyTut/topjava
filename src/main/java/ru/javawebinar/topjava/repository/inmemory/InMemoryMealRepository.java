package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);
    private static final Logger logger = LoggerFactory.getLogger(InMemoryMealRepository.class);

    {
        MealsUtil.MEALS.forEach(meal -> save(1, meal));
        MealsUtil.MEALS1.forEach(meal -> save(2, meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        logger.info("save meal{}, user id = {}", meal, userId);

        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(meal.getId(), meal);
            return meal;
        }
        return processUserMeals(userId, meals -> meals.computeIfPresent(meal.getId(), (k, ov) -> meal));
    }

    @Override
    public boolean delete(int userId, int id) {
        logger.info("delete meal {} of user {} ", id, userId);
        return (processUserMeals(userId, meals -> meals.remove(id)) != null);
    }

    @Override
    public Meal get(int userId, int id) {
        logger.info("get meal {} of user {}", id, userId);
        return processUserMeals(userId, meals -> meals.get(id));
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        logger.info("getting all for user {}", userId);
        return repository.getOrDefault(userId, new ConcurrentHashMap<Integer, Meal>()).values().stream()
                .sorted((m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getFilteredByDate(int userId, LocalDate startDate, LocalDate endDate) {
        logger.info("getting all filtered for user {}", userId);
        return getAll(userId).stream()
                .filter(meal -> DateTimeUtil.isBetween(meal.getDate(), startDate, endDate))
                .collect(Collectors.toList());
    }

    private <T> T processUserMeals(int userId, Function<Map<Integer, Meal>, T> function) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals != null) {
            return function.apply(userMeals);
        }
        return null;
    }

    public static void main(String[] args) {
        MealRepository mealRepository = new InMemoryMealRepository();
        ((InMemoryMealRepository) mealRepository).print();
//        mealRepository.delete(3, 10);
        Meal meal = new Meal(10, LocalDateTime.of(2019, Month.MAY, 30, 10, 0), "lunch", 50000);
        Meal meal_new = new Meal(LocalDateTime.of(2019, Month.MAY, 30, 10, 0), "new lunch", 5400);
        meal.setId(10);
        mealRepository.save(1, meal);
        System.out.println(mealRepository.save(1, meal));
        System.out.println(mealRepository.save(2, meal));
        System.out.println(mealRepository.save(3, meal_new));
        ((InMemoryMealRepository) mealRepository).print();
        System.out.println(mealRepository.delete(1, 1));
        System.out.println(mealRepository.delete(2, 4));
        ((InMemoryMealRepository) mealRepository).print();
        System.out.println(mealRepository.delete(3, 54));
        System.out.println("******************");
        System.out.println(mealRepository.get(2, 2));
        System.out.println(mealRepository.get(2, 11));
        System.out.println(mealRepository.get(100, 11));
        System.out.println(mealRepository.get(100, 100));
        System.out.println("****************************");
        System.out.println(mealRepository.getAll(1));
        System.out.println(mealRepository.getFilteredByDate(2, LocalDate.of(2015, 5, 21), LocalDate.of(2015, 5, 24)));
    }

    private void print() {
        repository.forEach((k, v) -> {
            System.out.println(k + ":");
            v.forEach((i, m) -> System.out.println(i + ": " + m));
            System.out.println("**************************");
        });
    }
}


package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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
        return repository.getOrDefault(userId, new ConcurrentHashMap<>()).values().stream()
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
}


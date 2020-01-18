package ru.javawebinar.topjava.storage;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class MemoryMealStorage implements MealStorage {
    private static Logger logger= getLogger(MemoryMealStorage.class);
    private static MemoryMealStorage instance = new MemoryMealStorage();
    private final ConcurrentMap<Integer, Meal> meals = new ConcurrentHashMap<>();
    private AtomicInteger count = new AtomicInteger(0);

    private MemoryMealStorage() {
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500));
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000));
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500));
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000));
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500));
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510));
    }

    public static MemoryMealStorage getInstance() {
        return instance;
    }

    @Override
    public void clear() {
        logger.debug("memory storage clear");
        meals.clear();
    }

    @Override
    public void update(Meal meal) {
        logger.debug("update meal with count: " + meal.getId());
        meals.replace(meal.getId(), meal);
    }

    @Override
    public void save(Meal meal) {
        int id = this.count.incrementAndGet();
        logger.debug("adding new meal, id: " + id);
        meal.setId(id);
        meals.put(id, meal);
    }

    @Override
    public Meal get(int id) {
        logger.debug("getting meal, count: " + id);
        return meals.get(id);
    }

    @Override
    public void delete(int id) {
        logger.debug("deleting meal, count " + id);
        meals.remove(id);
    }

    @Override
    public List<Meal> getAll() {
        logger.debug("getting all");
        return new ArrayList<>(meals.values());
    }

    @Override
    public int size() {
        logger.debug("getting storage size");
        return meals.size();
    }
}

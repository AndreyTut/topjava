package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MealService {

    private MealRepository repository;

    @Autowired
    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(int userId, Meal meal) {
        return repository.save(userId, meal);
    }

    public Meal get(int userId, int id) {
        return ValidationUtil.checkNotFoundWithId(repository.get(userId, id), id);
    }

    public Meal update(int userId, Meal meal) {
        return ValidationUtil.checkNotFoundWithId(repository.save(userId, meal), meal.getId());

    }

    public void delete(int userId, int id) {
        ValidationUtil.checkNotFoundWithId(repository.delete(userId, id), id);
    }

    public List<Meal> getAll(int userId) {
        return new ArrayList<>(repository.getAll(userId));
    }

    public List<Meal> getFilteredbyDate(int userId, LocalDate startDate, LocalDate endDate) {
        return new ArrayList<>(repository.getFilteredByDate(userId, startDate, endDate));
    }

}
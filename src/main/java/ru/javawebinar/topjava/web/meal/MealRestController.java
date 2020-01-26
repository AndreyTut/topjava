package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class MealRestController {
    private MealService service;
    private static final Logger logger = LoggerFactory.getLogger(MealRestController.class);

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal get(int id) {
        logger.info("getting meal {}", id);
        return service.get(SecurityUtil.authUserId(), id);
    }

    public Meal create(Meal meal) {
        logger.info("creating meal {}", meal);
        return service.create(SecurityUtil.authUserId(), meal);
    }

    public void update(Meal meal, int id) {
        logger.info("updating meal {} with id {}", meal, id);
        ValidationUtil.assureIdConsistent(meal, id);
        service.update(SecurityUtil.authUserId(), meal);
    }

    public void delete(int id) {
        logger.info("deleting meal {}", id);
        service.delete(SecurityUtil.authUserId(), id);
    }

    public List<MealTo> getAll() {
        logger.info("gettig all");
        return MealsUtil.getTos(service.getAll(SecurityUtil.authUserId()), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getFiltered(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTame) {
        logger.info("getting filtered");
        return MealsUtil.getFilteredTos(service.getFilteredbyDate(SecurityUtil.authUserId(), startDate == null ? LocalDate.MIN : startDate,
                endDate == null ? LocalDate.MAX : endDate), SecurityUtil.authUserCaloriesPerDay(), startTime == null ? LocalTime.MIN : startTime,
                endTame == null ? LocalTime.MAX : endTame);
    }
}
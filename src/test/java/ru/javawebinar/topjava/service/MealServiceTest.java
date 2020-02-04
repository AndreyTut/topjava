package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;


@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})

@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    private MealService service;

    @Autowired
    public void setService(MealService service) {
        this.service = service;
    }

    @Test
    public void get() {
        assertMatch(service.get(USER_MEAL1.getId(), USER_ID), USER_MEAL1);
    }

    @Test
    public void delete() {
        service.delete(USER_MEAL1.getId(), USER_ID);
        assertMatch(service.getAll(USER_ID), USER_MEAL6, USER_MEAL5, USER_MEAL4, USER_MEAL3, USER_MEAL2);
    }

    @Test
    public void getBetweenDates() {
        List<Meal> meals = service.getBetweenDates(USER_MEAL1.getDate(), USER_MEAL1.getDate(), USER_ID);
        assertMatch(meals, USER_MEAL3, USER_MEAL2, USER_MEAL1);
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(USER_ID), USER_MEAL6, USER_MEAL5, USER_MEAL4, USER_MEAL3, USER_MEAL2, USER_MEAL1);
        assertMatch(service.getAll(ADMIN_ID), ADMIN_MEAL2, ADMIN_MEAL1);
    }

    @Test
    public void update() {
        Meal meal = MealsUtil.createMealFromMeal(USER_MEAL1);
        meal.setDescription("updated meal");
        service.update(meal, USER_ID);
        assertMatch(service.get(USER_MEAL1.getId(), USER_ID), meal);
    }

    @Test
    public void create() {
        Meal newMeal = service.create(new Meal(LocalDateTime.now(), "new meal", 100500), USER_ID);
        assertMatch(service.getAll(USER_ID), newMeal, USER_MEAL6, USER_MEAL5, USER_MEAL4, USER_MEAL3, USER_MEAL2, USER_MEAL1);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotMine(){
        service.delete(USER_MEAL1.getId(), ADMIN_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getNotMine(){
        service.get(USER_MEAL1.getId(), ADMIN_ID);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotMine(){
        service.update(USER_MEAL1, ADMIN_ID);
    }
}
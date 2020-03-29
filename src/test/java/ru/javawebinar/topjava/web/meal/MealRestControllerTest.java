package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultActionsDsl;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.TestUtil;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.TestUtil.*;
import static ru.javawebinar.topjava.web.json.JsonUtil.*;
import static ru.javawebinar.topjava.web.meal.MealRestController.REST_URL;

class MealRestControllerTest extends AbstractControllerTest {

    private MealService service;

    @Autowired
    public void setService(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") MealService service) {
        this.service = service;
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/" + REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertMatchMealTo(readListFromJsonMvcResult(result, MealTo.class),
                        MEAL_TO7, MEAL_TO6, MEAL_TO5, MEAL_TO4, MEAL_TO3, MEAL_TO2, MEAL_TO1));
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/" + REST_URL + "/" + MEAL1_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertMatch(readFromJsonMvcResult(result, Meal.class), MEAL1));
    }

    @Test
    void create() throws Exception {
        Meal meal = getNew();
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post("/" + REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(meal)))
                .andExpect(status().isCreated());

        Meal created = readFromJson(action, Meal.class);
        meal.setId(created.getId());
        assertMatch(created, meal);
        assertMatch(created, service.get(created.getId(), SecurityUtil.authUserId()));
    }

    @Test
    void update() throws Exception {
        Meal meal = getUpdated();
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.put("/" + REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(meal)))
                .andExpect(status().isOk());

        assertMatch(service.get(meal.getId(), SecurityUtil.authUserId()), meal);
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/"+REST_URL+"/"+MEAL1_ID))
                .andExpect(status().isOk());
        assertThrows(NotFoundException.class, ()->service.get(MEAL1_ID, SecurityUtil.authUserId()));
    }

    @Test
    void getBetween() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"+REST_URL+"/filter?start=2015-05-30T12:00:00&end=2015-05-30T20:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertMatchMealTo(readListFromJsonMvcResult(result, MealTo.class), MEAL_TO3, MEAL_TO2));
    }
}
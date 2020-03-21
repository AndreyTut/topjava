package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

@Controller
@RequestMapping("/meals")
public class JspMealController {

    @Autowired
    MealService service;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("meals", MealsUtil.getTos(service.getAll(SecurityUtil.authUserId()), SecurityUtil.authUserCaloriesPerDay()));
        return "meals";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String delete(@RequestParam int id, Model model) {
        service.delete(id, SecurityUtil.authUserId());
        return "redirect: /topjava/meals";
    }

    @GetMapping("/update")
    public String update(@RequestParam int id, Model model) {
        model.addAttribute("meal", service.get(id, SecurityUtil.authUserId()));
        return "mealForm";
    }

    @GetMapping("/add")
    public String add(Model model) {
        return "mealForm";
    }

    @PostMapping
    public String save(@RequestParam String id,
                       @RequestParam String dateTime,
                       @RequestParam String description,
                       @RequestParam String calories,
                       Model model) {
        Meal meal = new Meal(LocalDateTime.parse(dateTime), description, Integer.parseInt(calories));

        if (!"".equals(id)) {
            meal.setId(Integer.parseInt(id));
            service.update(meal, SecurityUtil.authUserId());
        } else {
            service.create(meal, SecurityUtil.authUserId());
        }

        return "redirect: /topjava/meals";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam String startDate,
                         @RequestParam String endDate,
                         @RequestParam String startTime,
                         @RequestParam String endTime,
                         Model model) {
        int userId = SecurityUtil.authUserId();
        LocalDate sDate = convert(startDate, LocalDate::parse);
        LocalDate eDate = convert(endDate, LocalDate::parse);
        LocalTime sTime = convert(startTime, LocalTime::parse);
        LocalTime eTime = convert(endTime, LocalTime::parse);
        List<Meal> mealsDateFiltered = service.getBetweenDates(sDate, eDate, userId);
        model.addAttribute("meals", MealsUtil.getFilteredTos(mealsDateFiltered, SecurityUtil.authUserCaloriesPerDay(), sTime, eTime));
        return "meals";
    }

    private <T> T convert(String s, Function<String, T> f) {
        return "".equals(s) ? null : f.apply(s);
    }
}

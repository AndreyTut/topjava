package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.storage.MemoryMealStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class MealServlet extends HttpServlet {

    public static final Logger logger = LoggerFactory.getLogger(MealServlet.class);
    private MealStorage storage = MemoryMealStorage.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        logger.debug("meal servlet dopost");
        String idStr = req.getParameter("id");
        int id = (idStr == null || idStr.equals("")) ? -1 : Integer.parseInt(idStr);
        Meal meal = new Meal(id, LocalDateTime.parse(req.getParameter("time")),
                req.getParameter("description"), Integer.parseInt(req.getParameter("calories")));
        if (id == -1) {
            storage.save(meal);
        } else {
            storage.update(meal);
        }
        resp.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("redirect to meals");

        String action = req.getParameter("action");
        if (action == null) {
            List<Meal> meals = storage.getAll();
            List<MealTo> mealTos = MealsUtil.getFiltered(meals, LocalTime.MIN, LocalTime.MAX, 2000);
            req.setAttribute("meals", mealTos);
            req.getRequestDispatcher("WEB-INF/jsp/meals.jsp").forward(req, resp);
            return;
        }
        String idStr = req.getParameter("id");
        int id = -1;
        if (idStr != null) {
            id = Integer.parseInt(idStr);
        }
        switch (action) {
            case "delete":
                storage.delete(id);
                logger.debug("inside delete id=" + id);
                resp.sendRedirect("meals");
                return;
            case "edit":
                req.setAttribute("meal", storage.get(id));
                logger.debug("edit meal with id=" + id);
                break;
            case "add":
                req.setAttribute("meal", new Meal(LocalDateTime.MIN, "", 0));
                break;
        }
        req.getRequestDispatcher("WEB-INF/jsp/edit.jsp").forward(req, resp);
    }
}

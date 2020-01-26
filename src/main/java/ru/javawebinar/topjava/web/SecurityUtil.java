package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class SecurityUtil {

    private static int authUserId;
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public static void setAuthUserId(int authUserId) {
        logger.info("logged user id {}", authUserId);
        SecurityUtil.authUserId = authUserId;
    }

    public static int authUserId() {
        return authUserId;
    }

    public static int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }
}
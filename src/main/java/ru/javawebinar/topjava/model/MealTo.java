package ru.javawebinar.topjava.model;

import java.time.LocalDateTime;

public class MealTo {
    private int id;

    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    //    private final Supplier<Boolean> excess;
//    private final AtomicBoolean excess;
    private final boolean excess;


    public MealTo(int id, LocalDateTime dateTime, String description, int calories, boolean excess) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.excess = excess;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public boolean isExcess() {
        return excess;
    }

    //    public Boolean getExcess() {
//        return excess.get();
//    }

    @Override
    public String toString() {
        return "UserMealWithExceed{" +
                "dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                ", excess=" + excess +
                '}';
    }
}

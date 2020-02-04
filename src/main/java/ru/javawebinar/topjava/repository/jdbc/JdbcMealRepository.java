package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository("jdbcMealRepo")
public class JdbcMealRepository implements MealRepository {

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert simpleJdbcInsert;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Meal> rowMapper = new RowMapper<Meal>() {
        @Override
        public Meal mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Meal(rs.getInt("id"),
                    rs.getTimestamp("datetime").toLocalDateTime(),
                    rs.getString("description"),
                    rs.getInt("calories"));
        }
    };

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        SqlParameterSource source = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("datetime", meal.getDateTime())
                .addValue("user_id", userId);
        if (meal.isNew()) {
            meal.setId(simpleJdbcInsert.executeAndReturnKey(source).intValue());
        } else {
            if (namedParameterJdbcTemplate.update("UPDATE meals SET description=:description, calories=:calories, " +
                    "datetime=:datetime WHERE id=:id AND user_id=:user_id", source) == 0) {
                return null;
            }
        }
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("DELETE FROM meals WHERE id=? AND user_id=?", id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meals=jdbcTemplate.query("SELECT * FROM meals WHERE id=? and user_id=?", rowMapper, id, userId);
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query("SELECT * FROM meals WHERE user_id=? ORDER BY datetime DESC", rowMapper, userId);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        Timestamp startStamp = Timestamp.valueOf(startDate);
        Timestamp endStamp = Timestamp.valueOf(endDate);
        return jdbcTemplate.query("SELECT * FROM meals WHERE user_id=? and datetime>=? and datetime<=? ORDER BY datetime DESC", rowMapper, userId, startStamp, endStamp);
    }
}

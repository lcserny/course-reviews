package com.teamtreehouse.courses;

import com.google.gson.Gson;
import com.teamtreehouse.courses.dao.CourseDAO;
import com.teamtreehouse.courses.dao.ReviewDAO;
import com.teamtreehouse.courses.dao.Sql2oCourseDAO;
import com.teamtreehouse.courses.dao.Sql2oReviewDAO;
import com.teamtreehouse.courses.exc.ApiErrorException;
import com.teamtreehouse.courses.exc.DAOException;
import com.teamtreehouse.courses.model.Course;
import com.teamtreehouse.courses.model.Review;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import static spark.Spark.*;

/**
 * Created by user on 04.05.2016.
 */
public class Api
{
    public static void main(String[] args)
    {
        String dataSource = "jdbc:h2:~/reviews.db";
        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("java Api <port> <datasource>");
                System.exit(0);
            }
            port(Integer.parseInt(args[0]));
            dataSource = args[1];
        }

        Sql2o sql2o = new Sql2o(String.format("%s;INIT=RUNSCRIPT from 'classpath:db/init.sql'",
            dataSource), "", "");
        CourseDAO courseDAO = new Sql2oCourseDAO(sql2o);
        ReviewDAO reviewDAO = new Sql2oReviewDAO(sql2o);
        Gson gson = new Gson();

        post("/courses", "application/json", (request, response) -> {
            Course course = gson.fromJson(request.body(), Course.class);
            courseDAO.add(course);
            response.status(201);
            return course;
        }, gson::toJson);

        post("/courses/:courseId/reviews", "application/json", (request, response) -> {
            int courseId = Integer.parseInt(request.params("courseId"));
            Review review = gson.fromJson(request.body(), Review.class);
            review.setCourseId(courseId);
            try {
                reviewDAO.addReview(review);
            } catch (DAOException ex) {
                throw new ApiErrorException(500, ex.getMessage());
            }
            response.status(201);
            return review;
        }, gson::toJson);

        get("/courses", "application/json", (request, response) -> courseDAO.findAll(), gson::toJson);

        get("/courses/:id", "application/json", (request, response) -> {
            int id = Integer.parseInt(request.params("id"));
            Course course = courseDAO.findById(id);
            if (course == null) {
                throw new ApiErrorException(404, "Could not find course");
            }
            return course;
        }, gson::toJson);

        get("/courses/:courseId/reviews", "application/json", (request, response) -> {
            int courseId = Integer.parseInt(request.params("courseId"));
            return reviewDAO.findByCourseId(courseId);
        }, gson::toJson);

        after((request, response) -> {
            response.type("application/json");
        });

        exception(ApiErrorException.class, (exception, request, response) -> {
            ApiErrorException error = (ApiErrorException) exception;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", error.getStatus());
            jsonMap.put("errorMessage", error.getMessage());
            response.type("application/json");
            response.status(error.getStatus());
            response.body(gson.toJson(jsonMap));
        });
    }
}

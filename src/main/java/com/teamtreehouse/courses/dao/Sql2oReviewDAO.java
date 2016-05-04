package com.teamtreehouse.courses.dao;

import com.teamtreehouse.courses.exc.DAOException;
import com.teamtreehouse.courses.model.Course;
import com.teamtreehouse.courses.model.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

/**
 * Created by user on 04.05.2016.
 */
public class Sql2oReviewDAO implements ReviewDAO
{
    private final Sql2o sql2o;

    public Sql2oReviewDAO(Sql2o sql2o)
    {
        this.sql2o = sql2o;
    }

    @Override
    public void addReview(Review review) throws DAOException
    {
        String sql = "INSERT INTO reviews(course_id, rating, comment) VALUES(:courseId, :rating, :comment)";
        try (Connection connection = sql2o.open()) {
            int id = (int) connection.createQuery(sql)
                .bind(review)
                .executeUpdate()
                .getKey();
            review.setId(id);
        } catch (Sql2oException ex) {
            throw new DAOException(ex, "Problem adding review");
        }
    }

    @Override
    public List<Review> findAll() throws DAOException
    {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM reviews")
                .executeAndFetch(Review.class);
        }
    }

    @Override
    public List<Review> findByCourseId(int courseId) throws DAOException
    {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM reviews WHERE course_id = :courseId")
                .addColumnMapping("COURSE_ID", "courseId")
                .addParameter("courseId", courseId)
                .executeAndFetch(Review.class);
        }
    }
}

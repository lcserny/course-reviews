package com.teamtreehouse.courses.dao;

import com.teamtreehouse.courses.exc.DAOException;
import com.teamtreehouse.courses.model.Course;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

/**
 * Created by user on 04.05.2016.
 */
public class Sql2oCourseDAO implements CourseDAO
{
    private final Sql2o sql2o;

    public Sql2oCourseDAO(Sql2o sql2o)
    {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Course course) throws DAOException
    {
        String sql = "INSERT INTO courses(name, url) VALUES(:name, :url)";
        try (Connection connection = sql2o.open()) {
            int id = (int) connection.createQuery(sql)
                .bind(course)
                .executeUpdate()
                .getKey();
            course.setId(id);
        } catch (Sql2oException ex) {
            throw new DAOException(ex, "Problem adding course");
        }
    }

    @Override
    public List<Course> findAll() throws DAOException
    {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM courses")
                .executeAndFetch(Course.class);
        } catch (Sql2oException ex) {
            throw new DAOException(ex, "Problem getting courses");
        }
    }

    @Override
    public Course findById(int id)
    {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM courses WHERE id = :id")
                .addParameter("id", id)
                .executeAndFetchFirst(Course.class);
        }
    }

}

package com.teamtreehouse.courses.dao;

import com.teamtreehouse.courses.exc.DAOException;
import com.teamtreehouse.courses.model.Course;
import com.teamtreehouse.courses.model.Review;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by user on 04.05.2016.
 */
public class Sql2oReviewDAOTest
{
    private Sql2oReviewDAO reviewDao;
    private Sql2oCourseDAO courseDao;
    private Connection connection;
    private Course course;

    @Before
    public void setUp() throws Exception
    {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        reviewDao = new Sql2oReviewDAO(sql2o);
        courseDao = new Sql2oCourseDAO(sql2o);
        // Keep connection open through entire test so it isn't closed automatically
        connection = sql2o.open();
        course = new Course("Test", "http://test.com");
        courseDao.add(course);
    }

    @After
    public void tearDown() throws Exception
    {
        connection.close();
    }

    @Test
    public void addingReviewSetsNewId() throws Exception
    {
        Review review = new Review(course.getId(), 5, "Test comment");
        int originalId = review.getId();

        reviewDao.addReview(review);

        assertNotEquals(originalId, review.getId());
    }

    @Test
    public void multipleReviewsAreFoundWhenTheyExistsForACourse() throws Exception
    {
        reviewDao.addReview(new Review(course.getId(), 5, "Test comment"));
        reviewDao.addReview(new Review(course.getId(), 1, "Test comment"));

        List<Review> reviews = reviewDao.findByCourseId(course.getId());

        assertEquals(2, reviews.size());
    }

    @Test(expected = DAOException.class)
    public void addingAReviewToANonExistingCourseFails() throws Exception
    {
        Review review = new Review(5135, 5, "Test comment");

        reviewDao.addReview(review);
    }
}
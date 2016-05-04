package com.teamtreehouse.courses;

import com.google.gson.Gson;
import com.teamtreehouse.courses.dao.Sql2oCourseDAO;
import com.teamtreehouse.courses.dao.Sql2oReviewDAO;
import com.teamtreehouse.courses.model.Course;
import com.teamtreehouse.courses.model.Review;
import com.teamtreehouse.courses.testing.ApiClient;
import com.teamtreehouse.courses.testing.ApiResponse;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by user on 04.05.2016.
 */
public class ApiTest
{
    public static final String PORT = "4568";
    public static final String TEST_DATA_SROURCE = "jdbc:h2:mem:testing";
    Connection connection;
    private ApiClient client;
    private Gson gson;
    private Sql2oCourseDAO courseDao;
    private Sql2oReviewDAO reviewDao;

    @BeforeClass
    public static void startServer()
    {
        String[] args = {PORT, TEST_DATA_SROURCE};
        Api.main(args);
    }

    @AfterClass
    public static void stopServer()
    {
        Spark.stop();
    }

    @Before
    public void setUp() throws Exception
    {
        Sql2o sql2o = new Sql2o(TEST_DATA_SROURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
        courseDao = new Sql2oCourseDAO(sql2o);
        reviewDao = new Sql2oReviewDAO(sql2o);
        connection = sql2o.open();
        client = new ApiClient("http://localhost:" + PORT);
        gson = new Gson();
    }

    @After
    public void tearDown() throws Exception
    {
        connection.close();
    }

    @Test
    public void addingCoursesReturnsCreatedStatus() throws Exception
    {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Test");
        values.put("url", "http:test-something/jdbfgj");

        ApiResponse response = client.request("POST", "/courses", gson.toJson(values));

        assertEquals(201, response.getStatus());
    }

    @Test
    public void coursesCanBeAccessedById() throws Exception
    {
        Course course = newTestCourse();
        courseDao.add(course);

        ApiResponse response = client.request("GET", "/courses/" + course.getId());
        Course retrieved = gson.fromJson(response.getBody(), Course.class);

        assertEquals(course, retrieved);
    }

    @Test
    public void missingCoursesReturnNotFoundStatus() throws Exception
    {
        ApiResponse response = client.request("GET", "/courses/999999");

        assertEquals(404, response.getStatus());
    }

    @Test
    public void addingReviewGetsCreatedStatus() throws Exception
    {
        Course course = newTestCourse();
        courseDao.add(course);
        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Test comment");

        ApiResponse response = client.request("POST", String.format("/courses/%d/reviews", course.getId()), gson.toJson(values));

        assertEquals(201, response.getStatus());
    }

    @Test
    public void addingReviewToUnknownCourseThrowsError() throws Exception
    {
        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Test comment");

        ApiResponse response = client.request("POST", "/courses/99999/reviews", gson.toJson(values));

        assertEquals(500, response.getStatus());
    }

    @Test
    public void multipleReviewsReturnedForCourse() throws Exception
    {
        Course course = newTestCourse();
        courseDao.add(course);
        reviewDao.addReview(new Review(course.getId(), 5, "Test comment"));
        reviewDao.addReview(new Review(course.getId(), 4, "Test comment2"));

        ApiResponse response = client.request("GET", String.format("/courses/%d/reviews", course.getId()));
        Review[] reviews = gson.fromJson(response.getBody(), Review[].class);

        assertEquals(2, reviews.length);
    }

    private Course newTestCourse()
    {
        return new Course("test", "http://test.com");
    }
}

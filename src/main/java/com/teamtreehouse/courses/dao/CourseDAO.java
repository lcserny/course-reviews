package com.teamtreehouse.courses.dao;

import com.teamtreehouse.courses.exc.DAOException;
import com.teamtreehouse.courses.model.Course;

import java.util.List;

/**
 * Created by user on 04.05.2016.
 */
public interface CourseDAO
{
    void add(Course course) throws DAOException;

    List<Course> findAll() throws DAOException;

    Course findById(int id);
}

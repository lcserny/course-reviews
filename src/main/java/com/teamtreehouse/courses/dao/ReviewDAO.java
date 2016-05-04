package com.teamtreehouse.courses.dao;

import com.teamtreehouse.courses.exc.DAOException;
import com.teamtreehouse.courses.model.Review;

import java.util.List;

/**
 * Created by user on 04.05.2016.
 */
public interface ReviewDAO
{
    void addReview(Review review) throws DAOException;

    List<Review> findAll() throws DAOException;

    List<Review> findByCourseId(int courseId) throws DAOException;
}

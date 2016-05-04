package com.teamtreehouse.courses.exc;

/**
 * Created by user on 04.05.2016.
 */
public class DAOException extends Exception
{
    private final Exception originalException;

    public DAOException(Exception originalException, String message)
    {
        super(message);
        this.originalException = originalException;
    }
}

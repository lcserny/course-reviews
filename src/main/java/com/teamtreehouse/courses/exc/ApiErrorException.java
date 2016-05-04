package com.teamtreehouse.courses.exc;

/**
 * Created by user on 04.05.2016.
 */
public class ApiErrorException extends RuntimeException
{
    private final int status;

    public ApiErrorException(int status, String message)
    {
        super(message);
        this.status = status;
    }

    public int getStatus()
    {
        return status;
    }
}

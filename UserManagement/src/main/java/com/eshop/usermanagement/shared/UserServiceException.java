package com.eshop.usermanagement.shared;

public class UserServiceException extends RuntimeException{

    public UserServiceException(String message)
    {
        super(message);
    }
}

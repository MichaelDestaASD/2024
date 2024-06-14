package com.eshop.usermanagement.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotNull(message="First name cannot be null")
    @Size(min=2, message = "First name must not be less than 2 characters")
    private String firstName;


    @NotNull(message="Last name cannot be null")
    @Size(min=2, message = "Last name must not be less than 2 characters")
    private String lastName;

    @NotNull(message="Password cannot be null")
    @Size(min=8,max=16, message="Password must be equal or grater than 8 characters and less than 16 chaeracters")
    private String password;

    @NotNull(message="Email cannot be null")
    @Email
    private String email;
    private String role;
}

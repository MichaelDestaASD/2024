package com.eshop.usermanagement.data;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "userEntity")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
}

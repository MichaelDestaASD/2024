package com.eshop.usermanagement.service;

import com.eshop.usermanagement.model.UserResponse;

import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
   List<UserResponse> getUsers();
   UserResponse getUserByEmail(String email);
  void deleteUser(String userId, String authorizationHeader);
}

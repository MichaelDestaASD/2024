package com.eshop.usermanagement.controller;

import com.eshop.usermanagement.data.UserEntity;
import com.eshop.usermanagement.data.UserRepository;
import com.eshop.usermanagement.model.CreateUserRequest;
import com.eshop.usermanagement.model.LogInRequest;
import com.eshop.usermanagement.model.UserResponse;
import com.eshop.usermanagement.service.UserService;
import com.eshop.usermanagement.shared.JwtService;
import com.eshop.usermanagement.shared.UserServiceException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class Controller {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userDetailService;
    private final UserRepository userRepository;

    public Controller(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService, UserService userDetailService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
        this.userRepository = userRepository;
    }

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody LogInRequest logInRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                logInRequest.email(), logInRequest.password()
        ));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userDetailService.loadUserByUsername(logInRequest.email()));
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(createUserRequest.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserServiceException("User Already Exists");
        }
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(createUserRequest, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
        UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable("email") String email) {
        return new ResponseEntity<>(userDetailService.getUserByEmail(email), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return new ResponseEntity<>(userDetailService.getUsers(), HttpStatus.OK);
    }
}

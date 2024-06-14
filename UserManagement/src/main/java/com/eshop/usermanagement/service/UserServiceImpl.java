package com.eshop.usermanagement.service;

import com.eshop.usermanagement.data.UserEntity;
import com.eshop.usermanagement.data.UserRepository;
import com.eshop.usermanagement.model.UserResponse;
import com.eshop.usermanagement.shared.JwtService;
import com.eshop.usermanagement.shared.UserServiceException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    final UserRepository usersRepository;
    final JwtService jwtService;

    public UserServiceImpl(UserRepository usersRepository, JwtService jwtService) {
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        Optional<UserEntity> userEntity = usersRepository.findByEmail(email);

        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }
        UserEntity user = userEntity.get();
        return new ModelMapper().map(user, UserResponse.class);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> user = usersRepository.findByEmail(email);
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getEmail())
                    .password(userObj.getPassword())
                    .roles(getRoles(userObj))
                    .build();
        } else {
            throw new UsernameNotFoundException(email);
        }
    }

    private String[] getRoles(UserEntity user) {
        if (user.getRole() == null) {
            return new String[]{"USER"};
        }
        return user.getRole().split(",");
    }

    @Override
    public void deleteUser(String email, String authorizationHeader) {

        String userIdFromHeader = jwtService.extractUsername(email);

        if (!email.equalsIgnoreCase(userIdFromHeader)) {
            throw new UserServiceException("Operation not allowed");
        }

        Optional<UserEntity> userEntity = usersRepository.findByEmail(email);
        UserEntity user = null;
        if (userEntity.isPresent()) {
            user = userEntity.get();
        }
        if (userEntity.isEmpty())
            throw new UserServiceException("User not found");
        usersRepository.delete(user);

    }


    @Override
    public List<UserResponse> getUsers() {
        List<UserEntity> userEntities = usersRepository.findAll();

        if (userEntities.isEmpty())
            return new ArrayList<>();

        Type listType = new TypeToken<List<UserResponse>>() {
        }.getType();

        return new ModelMapper().map(userEntities, listType);
    }

}

package cn.edu.hdu.iamisfcaubackend.service;

import cn.edu.hdu.iamisfcaubackend.dto.CreateUserRequest;
import cn.edu.hdu.iamisfcaubackend.dto.UpdateUserRequest;
import cn.edu.hdu.iamisfcaubackend.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> listUsers(String name, String role);
    UserDto createUser(CreateUserRequest request);
    UserDto updateUser(String id, UpdateUserRequest request);
    void deleteUser(String id);
}